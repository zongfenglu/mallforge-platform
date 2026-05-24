package com.mallforge.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {
    private int code;
    private String message;
    private T data;
    private PageInfo pagination;

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = 0; r.message = "success"; r.data = data;
        return r;
    }
    public static <T> Result<T> ok() { return ok(null); }

    public static <T> Result<T> page(T data, long page, long size, long total) {
        Result<T> r = ok(data);
        r.pagination = new PageInfo(page, size, total);
        return r;
    }
    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code; r.message = message;
        return r;
    }
    public static <T> Result<T> fail(String message) { return fail(500, message); }

    @Data
    public static class PageInfo {
        private long page, size, total;
        public PageInfo(long page, long size, long total) {
            this.page = page; this.size = size; this.total = total;
        }
    }
}
