package com.example.utellme.action;

import com.example.utellme.ResultToolWindowFactory;
import com.example.utellme.common.CodeUtil;
import com.example.utellme.common.OllamaUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import java.io.IOException;

public class OptimizeCodeAction extends AnAction {

    private static final OllamaUtil ollamaUtil = new OllamaUtil();

    private static final CodeUtil codeUtil = new CodeUtil();

    @Override
    public void actionPerformed(AnActionEvent event) {
        // 获取编辑器
        Editor editor = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR);

        if (editor == null) {
            Messages.showMessageDialog("No active editor found!", "Warning", Messages.getWarningIcon());
            return;
        }

        // 获取选中的文本
        String selectedText = editor.getSelectionModel().getSelectedText();

        // 如果没有选中的文本，显示警告
        if (selectedText == null || selectedText.isEmpty()) {
            Messages.showMessageDialog("No code selected!", "Warning", Messages.getWarningIcon());
        } else {
            // 获取当前项目
            Project project = event.getProject();

            // 获取 ToolWindow 实例
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("UTellMe");

            if (toolWindow != null) {
                toolWindow.show(() -> {});

                // 运行后台任务来调用 Ollama 模型
                ProgressManager.getInstance().run(new Task.Backgroundable(project, "Thinking") {
                    @Override
                    public void run(ProgressIndicator indicator) {
                        try {
                            // 调用 Ollama 服务
                            String code=codeUtil.formatCode(selectedText);
                            String prompt = "你是一个Java代码开发专家，请根据给出的代码，提出相应的优化建议。生成回答输出格式分为四部分，分别为1：输入的代码，2：代码的解释，3：优化后的代码，4：优化点解释。待优化代码如下：" + code;
                            String result = ollamaUtil.callOllamaService("glm4", prompt);

                            // 更新工具窗口的 JTextArea
                            ApplicationManager.getApplication().invokeLater(() -> {
                                ResultToolWindowFactory.updateResult(result);
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                            ApplicationManager.getApplication().invokeLater(() -> {
                                Messages.showMessageDialog("Failed to call Ollama model!", "Error", Messages.getErrorIcon());
                            });
                        }
                    }
                });
            }
        }
    }
}
