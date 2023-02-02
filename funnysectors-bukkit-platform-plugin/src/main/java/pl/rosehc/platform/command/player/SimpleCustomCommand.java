package pl.rosehc.platform.command.player;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import pl.rosehc.adapter.helper.ChatHelper;
import pl.rosehc.platform.PlatformConfiguration.SimpleCustomCommandWrapper;
import pl.rosehc.platform.PlatformPlugin;

public interface SimpleCustomCommand {

  static SimpleCustomCommand compile(final SimpleCustomCommandWrapper wrapper)
      throws InstantiationException, IllegalAccessException {
    final ClassWriter writer = new ClassWriter(0);
    writer.visit(V1_8, ACC_PUBLIC + ACC_SUPER,
        "pl/rosehc/platform/command/player/SimpleCustomCommandImpl" + wrapper.name, null,
        "java/lang/Object", new String[]{"pl/rosehc/platform/command/player/SimpleCustomCommand"});

    {
      final MethodVisitor method = writer.visitMethod(ACC_PUBLIC, "handleCustomCommand",
          "(Lorg/bukkit/command/CommandSender;)V", null, null);
      AnnotationVisitor annotation = method.visitAnnotation(
          "Lme/vaperion/blade/annotation/Command;", true);
      final AnnotationVisitor valueArray = annotation.visitArray("value");
      valueArray.visit(null, wrapper.name);
      if (Objects.nonNull(wrapper.aliases) && !wrapper.aliases.isEmpty()) {
        wrapper.aliases.forEach(alias -> valueArray.visit(null, alias));
      }

      valueArray.visitEnd();
      if (Objects.nonNull(wrapper.description) && !wrapper.description.trim().isEmpty()) {
        annotation.visit("description", wrapper.description);
      }

      annotation.visitEnd();
      annotation = method.visitParameterAnnotation(0, "Lme/vaperion/blade/annotation/Sender;",
          true);
      annotation.visitEnd();

      final Label firstLabel = new Label();
      int lineNumber = 11;
      method.visitLabel(firstLabel);
      method.visitLineNumber(lineNumber++, firstLabel);
      method.visitCode();
      for (final String message : ChatHelper.colored(wrapper.messages)) {
        final Label messageLabel = new Label();
        method.visitLabel(messageLabel);
        method.visitLineNumber(lineNumber++, messageLabel);
        method.visitVarInsn(ALOAD, 1);
        if (message.contains("{PLAYER_NAME}")) {
          Iterator<String> iterator = Arrays.asList(message.split("\\{PLAYER_NAME}")).iterator();
          method.visitTypeInsn(NEW, "java/lang/StringBuilder");
          method.visitInsn(DUP);
          method.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
          while (iterator.hasNext()) {
            method.visitLdcInsn(iterator.next());
            method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            method.visitVarInsn(ALOAD, 1);
            method.visitMethodInsn(INVOKEINTERFACE, "org/bukkit/command/CommandSender", "getName",
                "()Ljava/lang/String;", true);
            method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            if (iterator.hasNext()) {
              method.visitLdcInsn(iterator.next());
              method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                  "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            }
          }

          method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
              "()Ljava/lang/String;", false);
        } else {
          method.visitLdcInsn(message);
        }

        method.visitMethodInsn(INVOKEINTERFACE, "org/bukkit/command/CommandSender", "sendMessage",
            "(Ljava/lang/String;)V", true);
      }

      final Label returnLabel = new Label();
      method.visitLabel(returnLabel);
      method.visitLineNumber(++lineNumber, returnLabel);
      method.visitInsn(RETURN);
      final Label localVariableInstantiationLabel = new Label();
      method.visitLabel(localVariableInstantiationLabel);
      method.visitLocalVariable("this",
          "Lpl/rosehc/platform/command/player/SimpleCustomCommandImpl" + wrapper.name + ";", null,
          firstLabel, localVariableInstantiationLabel, 0);
      method.visitLocalVariable("sender", "Lorg/bukkit/command/CommandSender;", null, firstLabel,
          localVariableInstantiationLabel, 1);
      method.visitMaxs(3, 2);
      method.visitEnd();
    }

    {
      final MethodVisitor method = writer.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
      final Label localClassInstantiationLabel = new Label();
      method.visitCode();
      method.visitLabel(localClassInstantiationLabel);
      method.visitLineNumber(7, localClassInstantiationLabel);
      method.visitVarInsn(ALOAD, 0);
      method.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
      method.visitInsn(RETURN);
      final Label localVariableInstantiationLabel = new Label();
      method.visitLabel(localVariableInstantiationLabel);
      method.visitLocalVariable("this",
          "Lpl/rosehc/platform/command/player/SimpleCustomCommandImpl" + wrapper.name + ";", null,
          localClassInstantiationLabel, localVariableInstantiationLabel, 0);
      method.visitMaxs(1, 1);
      method.visitEnd();
    }

    writer.visitEnd();
    return (SimpleCustomCommand) SimpleCustomCommandLoader.INSTANCE.create(wrapper.name,
        writer.toByteArray()).newInstance();
  }

  class SimpleCustomCommandLoader extends ClassLoader {

    private static final SimpleCustomCommandLoader INSTANCE = new SimpleCustomCommandLoader();

    private SimpleCustomCommandLoader() {
      super(PlatformPlugin.getInstance().getClass().getClassLoader());
    }

    private <T extends SimpleCustomCommandLoader> Class<T> create(final String name,
        final byte[] bytes) {
      //noinspection unchecked
      return (Class<T>) this.defineClass(
          "pl.rosehc.platform.command.player.SimpleCustomCommandImpl" + name, bytes, 0,
          bytes.length);
    }
  }
}
