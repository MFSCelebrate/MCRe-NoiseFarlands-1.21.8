/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.ObjectAllocator;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class FrameGraphBuilder {
    final private List<ResourceNode<?>> resourceNodes = new ArrayList();
    final private List<ObjectNode<?>> objectNodes = new ArrayList();
    final private List<FramePassImpl> passes = new ArrayList<FramePassImpl>();

    public FramePass createPass(String name) {
        FramePassImpl framePassImpl = new FramePassImpl(this.passes.size(), name);
        this.passes.add(framePassImpl);
        return framePassImpl;
    }

    public <T> net.minecraft.client.util.Handle<T> createObjectNode(String name, T object) {
        ObjectNode<T> objectNode = new ObjectNode<T>(name, null, object);
        this.objectNodes.add(objectNode);
        return objectNode.handle;
    }

    public <T> net.minecraft.client.util.Handle<T> createResourceHandle(String name, ClosableFactory<T> factory) {
        return this.createResourceNode((String)name, factory, null).handle;
    }

    <T> ResourceNode<T> createResourceNode(String name, ClosableFactory<T> factory, @Nullable FramePassImpl stageNode) {
        int i = this.resourceNodes.size();
        ResourceNode<T> resourceNode = new ResourceNode<T>(1, name, stageNode, factory);
        this.resourceNodes.add(resourceNode);
        return resourceNode;
    }

    public void run(ObjectAllocator allocator) {
        this.run(allocator, Profiler.NONE);
    }

    public void run(ObjectAllocator allocator, Profiler profiler) {
        BitSet bitSet = this.collectPassesToVisit();
        ArrayList<FramePassImpl> list = new ArrayList<FramePassImpl>(bitSet.cardinality());
        BitSet bitSet2 = new BitSet(this.passes.size());
        for (FramePassImpl framePassImpl : this.passes) {
            this.visit(framePassImpl, bitSet, bitSet2, list);
        }
        this.checkResources(list);
        for (FramePassImpl framePassImpl : list) {
            for (ResourceNode<?> resourceNode : framePassImpl.resourcesToAcquire) {
                profiler.acquire(resourceNode.name);
                resourceNode.acquire(allocator);
            }
            profiler.push(framePassImpl.name);
            framePassImpl.renderer.run();
            profiler.pop(framePassImpl.name);
            int i = framePassImpl.resourcesToRelease.nextSetBit(0);
            while (i >= 0) {
                ResourceNode<?> resourceNode;
                resourceNode = this.resourceNodes.get(i);
                profiler.release(resourceNode.name);
                resourceNode.release(allocator);
                i = framePassImpl.resourcesToRelease.nextSetBit(i + 1);
            }
        }
    }

    private BitSet collectPassesToVisit() {
        ArrayDeque<FramePassImpl> deque = new ArrayDeque<FramePassImpl>(this.passes.size());
        BitSet bitSet = new BitSet(this.passes.size());
        for (Node node : this.objectNodes) {
            FramePassImpl framePassImpl = node.handle.from;
            if (framePassImpl == null) continue;
            this.markForVisit(framePassImpl, bitSet, deque);
        }
        for (FramePassImpl framePassImpl : this.passes) {
            if (!framePassImpl.toBeVisited) continue;
            this.markForVisit(framePassImpl, bitSet, deque);
        }
        return bitSet;
    }

    private void markForVisit(FramePassImpl pass, BitSet result, Deque<FramePassImpl> deque) {
        deque.add(pass);
        while (!deque.isEmpty()) {
            FramePassImpl framePassImpl = deque.poll();
            if (result.get(framePassImpl.id)) continue;
            result.set(framePassImpl.id);
            int i = framePassImpl.requiredPassIds.nextSetBit(0);
            while (i >= 0) {
                deque.add(this.passes.get(i));
                i = framePassImpl.requiredPassIds.nextSetBit(i + 1);
            }
        }
    }

    private void visit(FramePassImpl node, BitSet unvisited, BitSet visiting, List<FramePassImpl> topologicalOrderOut) {
        if (visiting.get(node.id)) {
            String string = visiting.stream().mapToObj(id -> this.passes.get((int)id).name).collect(Collectors.joining(", "));
            throw new IllegalStateException("Frame graph cycle detected between " + string);
        }
        if (!unvisited.get(node.id)) {
            return;
        }
        visiting.set(node.id);
        unvisited.clear(node.id);
        int i = node.requiredPassIds.nextSetBit(0);
        while (i >= 0) {
            this.visit(this.passes.get(i), unvisited, visiting, topologicalOrderOut);
            i = node.requiredPassIds.nextSetBit(i + 1);
        }
        for (Handle<?> handle : node.transferredHandles) {
            int j = handle.dependents.nextSetBit(0);
            while (j >= 0) {
                if (j != node.id) {
                    this.visit(this.passes.get(j), unvisited, visiting, topologicalOrderOut);
                }
                j = handle.dependents.nextSetBit(j + 1);
            }
        }
        topologicalOrderOut.add(node);
        visiting.clear(node.id);
    }

    private void checkResources(Collection<FramePassImpl> passes) {
        FramePassImpl[] framePassImpls = new FramePassImpl[this.resourceNodes.size()];
        for (FramePassImpl framePassImpl : passes) {
            int i = framePassImpl.requiredResourceIds.nextSetBit(0);
            while (i >= 0) {
                ResourceNode<?> resourceNode = this.resourceNodes.get(i);
                FramePassImpl framePassImpl2 = framePassImpls[i];
                framePassImpls[i] = framePassImpl;
                if (framePassImpl2 == null) {
                    framePassImpl.resourcesToAcquire.add(resourceNode);
                } else {
                    framePassImpl2.resourcesToRelease.clear(i);
                }
                framePassImpl.resourcesToRelease.set(i);
                i = framePassImpl.requiredResourceIds.nextSetBit(i + 1);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class FramePassImpl
    implements FramePass {
        final int id;
        final String name;
        final List<Handle<?>> transferredHandles = new ArrayList();
        final BitSet requiredResourceIds = new BitSet();
        final BitSet requiredPassIds = new BitSet();
        Runnable renderer = () -> {};
        final List<ResourceNode<?>> resourcesToAcquire = new ArrayList();
        final BitSet resourcesToRelease = new BitSet();
        boolean toBeVisited;

        public FramePassImpl(int id, String name) {
            this.id = id;
            this.name = name;
        }

        private <T> void addRequired(Handle<T> handle) {
            Node node = handle.parent;
            if (node instanceof ResourceNode) {
                ResourceNode resourceNode = (ResourceNode)node;
                this.requiredResourceIds.set(resourceNode.id);
            }
        }

        private void addRequired(FramePassImpl child) {
            this.requiredPassIds.set(child.id);
        }

        @Override
        public <T> net.minecraft.client.util.Handle<T> addRequiredResource(String name, ClosableFactory<T> factory) {
            ResourceNode<T> resourceNode = FrameGraphBuilder.this.createResourceNode(name, factory, this);
            this.requiredResourceIds.set(resourceNode.id);
            return resourceNode.handle;
        }

        @Override
        public <T> void dependsOn(net.minecraft.client.util.Handle<T> handle) {
            this.dependsOn((Handle)handle);
        }

        private <T> void dependsOn(Handle<T> handle) {
            this.addRequired(handle);
            if (handle.from != null) {
                this.addRequired(handle.from);
            }
            handle.dependents.set(this.id);
        }

        @Override
        public <T> net.minecraft.client.util.Handle<T> transfer(net.minecraft.client.util.Handle<T> handle) {
            return this.transfer((Handle)handle);
        }

        @Override
        public void addRequired(FramePass pass) {
            this.requiredPassIds.set(((FramePassImpl)pass).id);
        }

        @Override
        public void markToBeVisited() {
            this.toBeVisited = true;
        }

        private <T> Handle<T> transfer(Handle<T> handle) {
            this.transferredHandles.add(handle);
            this.dependsOn(handle);
            return handle.moveTo(this);
        }

        @Override
        public void setRenderer(Runnable renderer) {
            this.renderer = renderer;
        }

        public String toString() {
            return this.name;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ObjectNode<T>
    extends Node<T> {
        final private T value;

        public ObjectNode(String name, @Nullable FramePassImpl parent, T value) {
            super(name, parent);
            this.value = value;
        }

        @Override
        public T get() {
            return this.value;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Handle<T>
    implements net.minecraft.client.util.Handle<T> {
        final Node<T> parent;
        final private int id;
        @Nullable
        final FramePassImpl from;
        final BitSet dependents = new BitSet();
        @Nullable
        private Handle<T> movedTo;

        Handle(Node<T> parent, int id, @Nullable FramePassImpl from) {
            this.parent = parent;
            this.id = id;
            this.from = from;
        }

        @Override
        public T get() {
            return this.parent.get();
        }

        Handle<T> moveTo(FramePassImpl pass) {
            if (this.parent.handle != this) {
                throw new IllegalStateException("Handle " + String.valueOf(this) + " is no longer valid, as its contents were moved into " + String.valueOf(this.movedTo));
            }
            Handle<T> handle = new Handle<T>(this.parent, this.id + 1, pass);
            this.parent.handle = handle;
            this.movedTo = handle;
            return handle;
        }

        public String toString() {
            if (this.from != null) {
                return String.valueOf(this.parent) + "#" + this.id + " (from " + String.valueOf(this.from) + ")";
            }
            return String.valueOf(this.parent) + "#" + this.id;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ResourceNode<T>
    extends Node<T> {
        final int id;
        final private ClosableFactory<T> factory;
        @Nullable
        private T resource;

        public ResourceNode(int id, String name, @Nullable FramePassImpl from, ClosableFactory<T> factory) {
            super(name, from);
            this.id = id;
            this.factory = factory;
        }

        @Override
        public T get() {
            return Objects.requireNonNull(this.resource, "Resource is not currently available");
        }

        public void acquire(ObjectAllocator allocator) {
            if (this.resource != null) {
                throw new IllegalStateException("Tried to acquire physical resource, but it was already assigned");
            }
            this.resource = allocator.acquire(this.factory);
        }

        public void release(ObjectAllocator allocator) {
            if (this.resource == null) {
                throw new IllegalStateException("Tried to release physical resource that was not allocated");
            }
            allocator.release(this.factory, this.resource);
            this.resource = null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Profiler {
        final static public Profiler NONE = new Profiler(){};

        default public void acquire(String name) {
        }

        default public void release(String name) {
        }

        default public void push(String location) {
        }

        default public void pop(String location) {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class Node<T> {
        final public String name;
        public Handle<T> handle;

        public Node(String name, @Nullable FramePassImpl from) {
            this.name = name;
            this.handle = new Handle(this, 0, from);
        }

        public abstract T get();

        public String toString() {
            return this.name;
        }
    }
}

