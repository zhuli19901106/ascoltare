package com.zhuli.ascoltate.server.util.bean;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.zhuli.ascoltate.server.util.BetterPageable;

import java.util.Optional;

@Component
public class BetterPageableArgumentResolver implements HandlerMethodArgumentResolver {
    private static final SortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new SortHandlerMethodArgumentResolver();

    private static final int maxPageSize = 200;
    private static final int maxPageNum = 100000;
    private static final int maxOffset = Integer.MAX_VALUE;
    private static final int maxLimit = 200;

    private int defaultPageSize = 15;
    private BetterPageable fallbackPageable = BetterPageable.of(0, defaultPageSize);
    private SortArgumentResolver sortResolver = DEFAULT_SORT_RESOLVER;
    private boolean oneIndexedParameters = false;

    public void setOneIndexedParameters(boolean bool) {
        this.oneIndexedParameters = bool;
    }

    public void setDefaultPageSize(int size) {
        this.defaultPageSize = size;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return BetterPageable.class.equals(parameter.getParameterType());
    }

    @Nullable
    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Optional<Integer> page = parseAndApplyBoundaries(webRequest.getParameter("page"), maxPageNum, true);
        Optional<Integer> size = parseAndApplyBoundaries(webRequest.getParameter("size"), maxPageSize, false);
        Optional<Integer> offset = parseAndApplyBoundaries(webRequest.getParameter("offset"), maxOffset, false);
        Optional<Integer> limit = parseAndApplyBoundaries(webRequest.getParameter("limit"), maxLimit, false);

        Sort orders = sortResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        BetterPageable fallbackPageable = getDefaultFromAnnotationOrFallback(parameter);

        if (orders.isUnsorted()) {
            orders = fallbackPageable.getSort();
        }
        if (offset.isPresent() || limit.isPresent()) {
            return BetterPageable.limitOf(offset.orElse(0), limit.orElse(defaultPageSize), orders);
        } else if (page.isPresent() || size.isPresent()) {
            return BetterPageable.of(page.orElse(0), size.orElse(defaultPageSize), orders);
        } else {
            return fallbackPageable;
        }
    }

    private Optional<Integer> parseAndApplyBoundaries(@Nullable String parameter, int upper, boolean shiftIndex) {
        if (!StringUtils.hasText(parameter)) {
            return Optional.empty();
        }

        try {
            int parsed = Integer.parseInt(parameter) - (oneIndexedParameters && shiftIndex ? 1 : 0);
            return Optional.of(parsed < 0 ? 0 : parsed > upper ? upper : parsed);
        } catch (NumberFormatException e) {
            return Optional.of(0);
        }
    }

    private BetterPageable getDefaultFromAnnotationOrFallback(MethodParameter methodParameter) {
        PageableDefault defaults = methodParameter.getParameterAnnotation(PageableDefault.class);

        if (defaults != null) {
            return BetterPageable.of(defaults.page(), defaults.size(), defaults.direction(), defaults.sort());
        }

        return fallbackPageable;
    }
}
