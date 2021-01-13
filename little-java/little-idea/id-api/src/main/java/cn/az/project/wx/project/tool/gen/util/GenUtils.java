package cn.az.wx.project.tool.gen.util;

import cn.az.wx.common.constant.GenerateConstants;
import cn.az.wx.common.utils.StringUtil;
import cn.az.wx.framework.config.GenConfig;
import cn.az.wx.project.tool.gen.domain.GenTable;
import cn.az.wx.project.tool.gen.domain.GenTableColumn;
import org.apache.commons.lang3.RegExUtils;

import java.util.Arrays;

/**
 * 代码生成器 工具类
 *
 * @author ruoyi
 */
public class GenUtils {
    /**
     * 初始化表信息
     */
    public static void initTable(GenTable genTable, String operName) {
        genTable.setClassName(convertClassName(genTable.getTableName()));
        genTable.setPackageName(GenConfig.getPackageName());
        genTable.setModuleName(getModuleName(GenConfig.getPackageName()));
        genTable.setBusinessName(getBusinessName(genTable.getTableName()));
        genTable.setFunctionName(replaceText(genTable.getTableComment()));
        genTable.setFunctionAuthor(GenConfig.getAuthor());
        genTable.setCreateBy(operName);
    }

    /**
     * 初始化列属性字段
     */
    public static void initColumnField(GenTableColumn column, GenTable table) {
        String dataType = getDbType(column.getColumnType());
        String columnName = column.getColumnName();
        column.setTableId(table.getTableId());
        column.setCreateBy(table.getCreateBy());
        // 设置java字段名
        column.setJavaField(StringUtil.toCamelCase(columnName));

        if (arraysContains(GenerateConstants.COLUMN_TYPE_STR, dataType)) {
            column.setJavaType(GenerateConstants.TYPE_STRING);
            // 字符串长度超过500设置为文本域
            Integer columnLength = getColumnLength(column.getColumnType());
            String htmlType = columnLength >= 500 ? GenerateConstants.HTML_TEXTAREA : GenerateConstants.HTML_INPUT;
            column.setHtmlType(htmlType);
        } else if (arraysContains(GenerateConstants.COLUMN_TYPE_TIME, dataType)) {
            column.setJavaType(GenerateConstants.TYPE_DATE);
            column.setHtmlType(GenerateConstants.HTML_DATETIME);
        } else if (arraysContains(GenerateConstants.COLUMN_TYPE_NUMBER, dataType)) {
            column.setHtmlType(GenerateConstants.HTML_INPUT);

            // 如果是浮点型
            String[] str = StringUtil.split(StringUtil.substringBetween(column.getColumnType(), "(", ")"), ",");
            if (str != null && str.length == 2 && Integer.parseInt(str[1]) > 0) {
                column.setJavaType(GenerateConstants.TYPE_DOUBLE);
            }
            // 如果是整形
            else if (str != null && str.length == 1 && Integer.parseInt(str[0]) <= 10) {
                column.setJavaType(GenerateConstants.TYPE_INTEGER);
            }
            // 长整形
            else {
                column.setJavaType(GenerateConstants.TYPE_LONG);
            }
        }

        // 插入字段（默认所有字段都需要插入）
        column.setIsInsert(GenerateConstants.REQUIRE);

        // 编辑字段
        if (!arraysContains(GenerateConstants.COLUMN_NAME_NOT_EDIT, columnName) && !column.isPk()) {
            column.setIsEdit(GenerateConstants.REQUIRE);
        }
        // 列表字段
        if (!arraysContains(GenerateConstants.COLUMN_NAME_NOT_LIST, columnName) && !column.isPk()) {
            column.setIsList(GenerateConstants.REQUIRE);
        }
        // 查询字段
        if (!arraysContains(GenerateConstants.COLUMN_NAME_NOT_QUERY, columnName) && !column.isPk()) {
            column.setIsQuery(GenerateConstants.REQUIRE);
        }

        // 查询字段类型
        if (StringUtil.endsWithIgnoreCase(columnName, "name")) {
            column.setQueryType(GenerateConstants.QUERY_LIKE);
        }
        // 状态字段设置单选框
        if (StringUtil.endsWithIgnoreCase(columnName, "status")) {
            column.setHtmlType(GenerateConstants.HTML_RADIO);
        }
        // 类型&性别字段设置下拉框
        else if (StringUtil.endsWithIgnoreCase(columnName, "type")
            || StringUtil.endsWithIgnoreCase(columnName, "sex")) {
            column.setHtmlType(GenerateConstants.HTML_SELECT);
        }
    }

    /**
     * 校验数组是否包含指定值
     *
     * @param arr         数组
     * @param targetValue 值
     * @return 是否包含
     */
    public static boolean arraysContains(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    /**
     * 获取模块名
     *
     * @param packageName 包名
     * @return 模块名
     */
    public static String getModuleName(String packageName) {
        int lastIndex = packageName.lastIndexOf(".");
        int nameLength = packageName.length();
        return StringUtil.substring(packageName, lastIndex + 1, nameLength);
    }

    /**
     * 获取业务名
     *
     * @param tableName 表名
     * @return 业务名
     */
    public static String getBusinessName(String tableName) {
        int lastIndex = tableName.lastIndexOf("_");
        int nameLength = tableName.length();
        return StringUtil.substring(tableName, lastIndex + 1, nameLength);
    }

    /**
     * 表名转换成Java类名
     *
     * @param tableName 表名称
     * @return 类名
     */
    public static String convertClassName(String tableName) {
        boolean autoRemovePre = GenConfig.getAutoRemovePre();
        String tablePrefix = GenConfig.getTablePrefix();
        if (autoRemovePre && StringUtil.isNotEmpty(tablePrefix)) {
            String[] searchList = StringUtil.split(tablePrefix, ",");
            tableName = replaceFirst(tableName, searchList);
        }
        return StringUtil.convertToCamelCase(tableName);
    }

    /**
     * 批量替换前缀
     *
     * @param replacementm 替换值
     * @param searchList   替换列表-
     */
    public static String replaceFirst(String replacementm, String[] searchList) {
        String text = replacementm;
        for (String searchString : searchList) {
            if (replacementm.startsWith(searchString)) {
                text = replacementm.replaceFirst(searchString, "");
                break;
            }
        }
        return text;
    }

    /**
     * 关键字替换
     *
     * @param text 需要被替换的名字
     * @return 替换后的名字
     */
    public static String replaceText(String text) {
        return RegExUtils.replaceAll(text, "(?:表|若依)", "");
    }

    /**
     * 获取数据库类型字段
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static String getDbType(String columnType) {
        if (StringUtil.indexOf(columnType, "(") > 0) {
            return StringUtil.substringBefore(columnType, "(");
        } else {
            return columnType;
        }
    }

    /**
     * 获取字段长度
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static Integer getColumnLength(String columnType) {
        if (StringUtil.indexOf(columnType, "(") > 0) {
            String length = StringUtil.substringBetween(columnType, "(", ")");
            return Integer.valueOf(length);
        } else {
            return 0;
        }
    }
}
