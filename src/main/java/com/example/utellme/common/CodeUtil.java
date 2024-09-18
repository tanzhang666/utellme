package com.example.utellme.common;

public class CodeUtil {

    public String formatCode(String code){
        // 删除行注释
        String withoutComments = code.replaceAll("//.*", "");

        // 删除空行和多余的空格
        String formattedCode = withoutComments
                .replaceAll("(?m)^[ \t]*\r?\n", "")  // 删除空行
                .trim();  // 去除首尾多余的空格

        // 转义代码中的换行符和引号
        return formattedCode.replace("\n", "\\n").replace("\"", "\\\"");
    }

    public String formatComment(String comment){
        // 删除空行和多余的空格
        String formattedCode = comment
                .replaceAll("(?m)^[ \t]*\r?\n", "")  // 删除空行
                .trim();  // 去除首尾多余的空格
        // 转义代码中的换行符和引号
        return formattedCode.replace("\n", "\\n").replace("\"", "\\\"");
    }

}
