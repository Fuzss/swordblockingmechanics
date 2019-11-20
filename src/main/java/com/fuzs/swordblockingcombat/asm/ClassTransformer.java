package com.fuzs.swordblockingcombat.asm;

import com.fuzs.swordblockingcombat.SwordBlockingCombat;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

@SuppressWarnings("unused")
public class ClassTransformer implements IClassTransformer {

    private static final String[] classesBeingTransformed = {"net.minecraft.client.model.ModelBiped"};

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        boolean obfuscated = !name.equals(transformedName);
        int index = Arrays.asList(classesBeingTransformed).indexOf(transformedName);
        return index != -1 ? transform(index, basicClass, obfuscated) : basicClass;

    }

    private static byte[] transform(int index, byte[] basicClass, boolean obfuscated) {

        SwordBlockingCombat.LOGGER.info("Patching " + classesBeingTransformed[index] + "...");
        System.out.println(obfuscated);

        try {

            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);
            transformServerWorldEventHandler(classNode, obfuscated);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);

            return classWriter.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return basicClass;

    }

    private static void transformServerWorldEventHandler(ClassNode serverWorldEventHandlerClass, boolean obfuscated) {

        String setRotationAnglesName = obfuscated ? "func_212844_a_" : "setRotationAngles";
        String setRotationAnglesDescriptor = "(FFFFFFLnet/minecraft/entity/Entity;)V";

        String swingProgress = obfuscated ? "field_217112_c" : "swingProgress";
        String rotateAngleY = obfuscated ? "field_78796_g" : "rotateAngleY";
        String rightArm = obfuscated ? "field_178723_h" : "bipedRightArm";
        String leftArm = obfuscated ? "field_178724_i" : "bipedLeftArm";

        AbstractInsnNode foundNode = null;

        for (MethodNode method : serverWorldEventHandlerClass.methods) {

            if (method.name.equals(setRotationAnglesName) && method.desc.equals(setRotationAnglesDescriptor)) {

                for (AbstractInsnNode node : method.instructions.toArray()) {

                    if (node instanceof FieldInsnNode && node.getOpcode() == GETFIELD && ((FieldInsnNode) node).name.equals(swingProgress)) {

                        AbstractInsnNode prevNode = getNthNode(node, -5);
                        if (prevNode instanceof FieldInsnNode && prevNode.getOpcode() == PUTFIELD && ((FieldInsnNode) prevNode).name.equals(rotateAngleY)) {
                            foundNode = node;
                            break;
                        }

                    }

                }

                if (foundNode != null) {

                    InsnList insnList = new InsnList();

                    insnList.add(new VarInsnNode(ALOAD, 0));
                    insnList.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelBiped", rightArm, "Lnet/minecraft/client/model/ModelRenderer;"));
                    insnList.add(new VarInsnNode(ALOAD, 0));
                    insnList.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelBiped", leftArm, "Lnet/minecraft/client/model/ModelRenderer;"));
                    insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(ThirdPersonBlockingHook.class), "setArmRotationAngel", "(Lnet/minecraft/client/model/ModelRenderer;Lnet/minecraft/client/model/ModelRenderer;)V", false));

                    method.instructions.insertBefore(getNthNode(foundNode, -2), insnList);

                }

                break;

            }
        }

        SwordBlockingCombat.LOGGER.info("Patching " + (foundNode != null ? "was successful" : "failed"));

    }

    private static AbstractInsnNode getNthNode(AbstractInsnNode node, int n) {

        for (int i = 0; i < Math.abs(n); i++) {
            if (n < 0) {
                node = node.getPrevious();
            } else {
                node = node.getNext();
            }
        }

        return node;

    }

}
