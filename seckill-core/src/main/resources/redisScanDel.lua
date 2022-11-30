---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by mdmbct.
--- DateTime: 2022/11/29 下午9:22
--- redis-cli -x script load < /home/mdmbct/Workspace/Projects/IdeaProjects/Java/DistributedSeckill/seckill-core/src/main/resources/redisScanDel.lua
---"890c83948d798d794027afd8160e847d94d4c95d"
--- evalsha 890c83948d798d794027afd8160e847d94d4c95d 1 DSK:SeckillTest:* 1000
--- evalsha 890c83948d798d794027afd8160e847d94d4c95d 2 DSK:SeckillTest:* {DSK:SeckillTest:* 1000
---
local function scan(key)
    local cursor = 0
    local keyNum = 0

    repeat
        local res = redis.call("scan", cursor, "match", key, 'COUNT', ARGV[1])
        if (res ~= nil and #res >= 0) then
            redis.replicate_commands()
            cursor = tonumber(res[1])
            local keys = res[2]
            keyNum = #keys
            for i = 1, keyNum, 1 do
                local k = tostring(keys[i])
                redis.call("del", k)
            end
        end
    until (cursor <= 0)

    return keyNum
end

local len = #KEYS
local i = 1
while (i <= len)
do
    scan(KEYS[i])
    i = i + 1
end

--local len = #KEYS
--local i = 1
--local total = 0
--while (i <= len)
--do
--    total = total + scan(KEYS[i])
--    i = i + 1
--end
--
--return total