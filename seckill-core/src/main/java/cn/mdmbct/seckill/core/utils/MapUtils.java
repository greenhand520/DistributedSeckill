package cn.mdmbct.seckill.core.utils;

import java.util.Map;
import java.util.function.Function;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/11 上午9:19
 * @modified mdmbct
 * @since 1.0
 */
public class MapUtils {

    /**
     * 如果 key 对应的 value 不存在，则使用获取 mappingFunction 重新计算后的值，并保存为该 key 的 value，否则返回 value。<br>
     * 方法来自Dubbo，解决使用ConcurrentHashMap.computeIfAbsent导致的死循环问题。（issues#2349）<br>
     * A temporary workaround for Java 8 specific performance issue JDK-8161372 .<br>
     * This class should be removed once we drop Java 8 support.
     *
     * @param <K>             键类型
     * @param <V>             值类型
     * @param map             Map
     * @param key             键
     * @param mappingFunction 值不存在时值的生成函数
     * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8161372">https://bugs.openjdk.java.net/browse/JDK-8161372</a>
     * @return 值
     */
    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<? super K, ? extends V> mappingFunction) {
        V value = map.get(key);
        if (null == value) {
            map.putIfAbsent(key, mappingFunction.apply(key));
            value = map.get(key);
        }
        return value;
    }
}
