package com.aliyahatzoff.utils;


import android.content.Context;

import com.daasuu.gpuv.egl.filter.GlBrightnessFilter;
import com.daasuu.gpuv.egl.filter.GlExposureFilter;
import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.filter.GlGammaFilter;
import com.daasuu.gpuv.egl.filter.GlHazeFilter;
import com.daasuu.gpuv.egl.filter.GlInvertFilter;
import com.daasuu.gpuv.egl.filter.GlMonochromeFilter;
import com.daasuu.gpuv.egl.filter.GlPixelationFilter;
import com.daasuu.gpuv.egl.filter.GlPosterizeFilter;
import com.daasuu.gpuv.egl.filter.GlSepiaFilter;
import com.daasuu.gpuv.egl.filter.GlSharpenFilter;
import com.daasuu.gpuv.egl.filter.GlSolarizeFilter;
import com.daasuu.gpuv.egl.filter.GlVignetteFilter;

import java.util.Arrays;
import java.util.List;

// this is the all available filters
public enum FilterType {
    DEFAULT,
    NONE,
    BRIGHTNESS,
    EXPOSURE,
    GAMMA,
    GRAYSCALE,
    HAZE,
    INVERT,
    MONOCHROME,
    PIXELATED,
    POSTERIZE,
    SEPIA,
    SHARP,
    SOLARIZE,
    VIGNETTE;

    public static List<FilterType> createFilterList() {
        return Arrays.asList(FilterType.values());
    }

    public static GlFilter createGlFilter(FilterType filterType, Context context) {
        switch (filterType) {
            case NONE:
                return new GlFilter();
            case BRIGHTNESS:
                GlBrightnessFilter glBrightnessFilter = new GlBrightnessFilter();
                glBrightnessFilter.setBrightness(0.2f);
                return glBrightnessFilter;
            case EXPOSURE:
                return new GlExposureFilter();
            case GAMMA:
                GlGammaFilter glGammaFilter = new GlGammaFilter();
                glGammaFilter.setGamma(2f);
                return glGammaFilter;
            case HAZE:
                GlHazeFilter glHazeFilter = new GlHazeFilter();
                glHazeFilter.setSlope(-0.5f);
                return glHazeFilter;
            case MONOCHROME:
                return new GlMonochromeFilter();
            case POSTERIZE:
                return new GlPosterizeFilter();
            case PIXELATED:
                GlPixelationFilter glf = new GlPixelationFilter();
                glf.setPixel(5);
                return glf;

                case SEPIA:
                return new GlSepiaFilter();
            case SHARP:
                GlSharpenFilter glSharpenFilter = new GlSharpenFilter();
                glSharpenFilter.setSharpness(1f);
                return glSharpenFilter;
            case SOLARIZE:
                return new GlSolarizeFilter();
            case VIGNETTE:
                return new GlVignetteFilter();
            case INVERT:
                return new GlInvertFilter();
              default:
                return new GlFilter();
        }
    }


}

