package com.example.utellme;

import com.example.utellme.common.CodeUtil;
import com.example.utellme.common.OllamaUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class AskWindowFactory implements ToolWindowFactory {

    private static JTextArea resultTextArea;// 保持静态引用

    private static JTextArea questionTextArea;

    private static JButton askButton;

    private static OllamaUtil ollamaUtil= new OllamaUtil();

    private static CodeUtil codeUtil= new CodeUtil();

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        // 创建工具窗口内容面板
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10)); // 使用边界布局，并设置间距

        // 问题输入区
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout(5, 5));

        questionTextArea = new JTextArea(3, 50); // 输入区域设置为3行
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        JBScrollPane questionScrollPane = new JBScrollPane(questionTextArea);
        inputPanel.add(new JLabel("输入问题:"), BorderLayout.NORTH);
        inputPanel.add(questionScrollPane, BorderLayout.CENTER);

        // 提交按钮
        askButton = new JButton("提交问题");
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // 居中按钮
        buttonPanel.add(askButton);
        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 结果输出区
        resultTextArea = new JTextArea(10, 50); // 结果区域设置为10行
        resultTextArea.setEditable(false);  // 结果区不可编辑
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        JBScrollPane resultScrollPane = new JBScrollPane(resultTextArea);

        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout(5, 5));
        outputPanel.add(new JLabel("输出结果:"), BorderLayout.NORTH);
        outputPanel.add(resultScrollPane, BorderLayout.CENTER);

        // 添加输入区和输出区到主面板
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(outputPanel, BorderLayout.CENTER);

        // 处理按钮点击事件
        askButton.addActionListener(event -> {
            // 获取问题文本
            String question = questionTextArea.getText();
            if (!question.isEmpty()) {
                try {
                    // 调用 Ollama 服务
                    String formatComment = codeUtil.formatComment(question);
                    System.out.println(formatComment);
                    String prompt = formatComment;
                    String result = ollamaUtil.callOllamaService("glm4", prompt);

                    // 更新工具窗口的 JTextArea
                    ApplicationManager.getApplication().invokeLater(() -> {
                        updateResult(result);
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Messages.showMessageDialog("Failed to call Ollama model!", "Error", Messages.getErrorIcon());
                    });
                }
            } else {
                updateResult("请输入问题！");
            }
        });
        // 创建 Content 并将面板设置为工具窗口的内容
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    // 提供一个更新 JTextArea 的方法
    public static void updateResult(String result) {
        if (resultTextArea != null) {
            resultTextArea.setText(result);
        }
    }
}