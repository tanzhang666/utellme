package com.example.utellme;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import java.awt.*;

public class ResultToolWindowFactory implements ToolWindowFactory {

    private static JTextArea resultTextArea;  // 保持静态引用

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        // 创建工具窗口内容面板
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // 创建可滚动的 JTextArea，用于显示执行结果
        resultTextArea = new JTextArea();
        resultTextArea.setEditable(true);
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        // 设置输出框的字体，使用 "JetBrains Mono" 字体，大小为 14
        resultTextArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        JBScrollPane scrollPane = new JBScrollPane(resultTextArea);

        // 将滚动面板添加到主面板
        panel.add(scrollPane);

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