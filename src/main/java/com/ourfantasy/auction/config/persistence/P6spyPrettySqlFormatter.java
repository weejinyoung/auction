package com.ourfantasy.auction.config.persistence;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;
import java.util.regex.Pattern;

public class P6spyPrettySqlFormatter implements MessageFormattingStrategy {

    private static final String NEW_LINE = System.lineSeparator();
    private static final String REGEX_PREFIX = "\\(";
    private static final String REGEX_SUFFIX = ".+?\\)";
    private static final String SELECT_PATTERN = "^(select)\\s";
    private static final String INSERT_PATTERN = "^(insert)\\s";
    private static final String UPDATE_PATTERN = "^(update)\\s";
    private static final String DELETE_PATTERN = "^(delete)\\s";

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        
        sb.append("\n")
          .append("┌───────────────────────────────────────────────────────────────────────────────────────┐").append(NEW_LINE)
          .append("│ 시간: ").append(sdf.format(new Date())).append(NEW_LINE)
          .append("│ 소요시간: ").append(elapsed).append("ms").append(NEW_LINE)
          .append("│ 연결ID: ").append(connectionId);
        
        if (sql != null && !sql.trim().isEmpty()) {
            // 파라미터가 들어가기 전의 SQL문
            if (category.equals(Category.STATEMENT.getName())) {
                sb.append(NEW_LINE).append("│ 쿼리: ").append(NEW_LINE).append(NEW_LINE);
                sb.append(formatSql(sql));
            } 
            // 파라미터가 들어간 SQL문
            else {
                sb.append(NEW_LINE).append("│ 실행된 쿼리: ").append(NEW_LINE).append(NEW_LINE);
                sb.append(formatSql(sql));
            }
        }
        
        sb.append(NEW_LINE)
          .append("└───────────────────────────────────────────────────────────────────────────────────────┘");
        
        return sb.toString();
    }
    
    private String formatSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) return "";
        
        // 큰 따옴표, 작은 따옴표 제거
        sql = sql.replaceAll("\'", "").replaceAll("\"", "");
        
        // SQL 종류 확인 후 포맷팅
        if (Pattern.compile(SELECT_PATTERN, Pattern.CASE_INSENSITIVE).matcher(sql).find()) {
            return FormatStyle.BASIC.getFormatter().format(sql);
        } else if (Pattern.compile(INSERT_PATTERN, Pattern.CASE_INSENSITIVE).matcher(sql).find()) {
            return FormatStyle.BASIC.getFormatter().format(sql);
        } else if (Pattern.compile(UPDATE_PATTERN, Pattern.CASE_INSENSITIVE).matcher(sql).find()) {
            return FormatStyle.BASIC.getFormatter().format(sql);
        } else if (Pattern.compile(DELETE_PATTERN, Pattern.CASE_INSENSITIVE).matcher(sql).find()) {
            return FormatStyle.BASIC.getFormatter().format(sql);
        } else {
            return "\t" + sql;
        }
    }
}