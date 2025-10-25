package com.rejs.orm.session.metadata.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NamingUtilsTest {

    /**
     * GEMINI로 생성
     */
    private static final String[][] TEST_CASES = {
            {"camelCase", "camel_case"},
            {"PascalCase", "pascal_case"},
            {"myVariableName", "my_variable_name"},
            {"HTMLParser", "html_parser"},         // 약어 (시작)
            {"parseHTMLData", "parse_html_data"}, // 약어 (중간)
            {"myHTTPRequest", "my_http_request"}, // 약어 (끝)
            {"HTTPRequest", "http_request"},       // 약어 (전체)
            {"HTTP", "http"},                      // 약어 (단일)
            {"version1To2", "version1_to2"},       // 숫자 포함
            {"myV2App", "my_v2_app"},              // 숫자 + 대문자
            {"a", "a"},                            // 단일 문자 (소)
            {"A", "a"},                            // 단일 문자 (대)
            {"word", "word"},                      // 단일 단어 (소)
            {"WORD", "word"}                       // 단일 단어 (대)
    };

    @Test
    void camelToSnake() {
        for (String[] pair : TEST_CASES){
            String q = pair[0];
            String a = pair[1];

            assertEquals(a,NamingUtils.camelToSnake(q));
        }
    }
}