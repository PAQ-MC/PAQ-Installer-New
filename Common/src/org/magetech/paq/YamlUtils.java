package org.magetech.paq;

import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;

/**
 * Created by Aleksander on 08.12.13.
 */
public class YamlUtils {
    private static <T extends Object> Construct chainConstruct(final Construct inner, final Class<T> type, final TConstruct<T> construct) {
        Assert.notNull(construct, "construct");
        Assert.notNull(inner, "inner");

        return new Construct() {
            @Override
            public Object construct(Node node) {
                if(node.getType().equals(type)) {
                    Out<T> out = new Out<T>();
                    if(construct.parse((ScalarNode)node, out)) {
                        return out.getValue();
                    }
                }
                return inner.construct(node);
            }

            @Override
            public void construct2ndStep(Node node, Object object) {
                inner.construct2ndStep(node, object);
            }
        };
    }

    public static abstract class TConstruct<T> {
        public abstract boolean parse(ScalarNode node, Out<T> result);
    }

    public static class ChainConstructor extends CustomClassLoaderConstructor {
        public ChainConstructor(Class<? extends Object> type)
        {
            super(type, ContextUtils.get());
        }

        public <T extends Object> void addConstructor(Class<T> type, TConstruct<T> constructor) {
            yamlClassConstructors.put(NodeId.scalar, chainConstruct(yamlClassConstructors.get(NodeId.scalar), type, constructor));
        }
    }
}
