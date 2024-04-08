//
// Copyright (c) 2024 Couchbase, Inc All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package com.couchbase.lite.java.colors.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class NamedColor {
    private static int parseColor(@NonNull String colorCode) {
        int c = Integer.parseInt(colorCode);
        if ((c >= 0) && (c <= 255)) { return c; }
        throw new NumberFormatException("Color code must be between 0 and 255: " + colorCode);
    }


    @NonNull
    private final String id;
    @NonNull
    private final String name;
    private final double distance;

    private final int red;
    private final int green;
    private final int blue;

    public NamedColor(
        @NonNull String id,
        @NonNull String name,
        @NonNull String colorCode,
        double distance) {

        this.id = id;
        this.name = name;
        this.distance = distance;

        if (colorCode.startsWith("#")) { colorCode = colorCode.substring(1); }

        if (colorCode.length() == 6) {
            final long color = Long.parseLong(colorCode.substring(1), 16);
            red = (int) (color >> 16 & 255);
            green = (int) (color >> 8 & 255);
            blue = (int) (color & 255);
            return;
        }

        String[] rgb = colorCode.split(" ");
        if (rgb.length == 3) {
            try {
                red = parseColor(rgb[0]);
                green = parseColor(rgb[1]);
                blue = parseColor(rgb[2]);
                return;
            }
            catch (Exception ignore) { }
        }

        throw new IllegalArgumentException("Unparsable color: " + colorCode);
    }

    public NamedColor(
        @NonNull String id,
        @NonNull String name,
        @NonNull String red,
        @NonNull String green,
        @NonNull String blue,
        double distance) {
        this(id, name, parseColor(red), parseColor(green), parseColor(blue), distance);
    }

    public NamedColor(
        @NonNull String id,
        @NonNull String name,
        int red,
        int green,
        int blue,
        double distance) {
        this.id = id;
        this.name = name;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.distance = distance;
    }

    @NonNull
    public final String getId() { return id; }

    @NonNull
    public final String getName() { return name; }

    public final double getDistance() { return distance; }

    public String getColorCode() {
        return "#" + Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue);
    }

    public List<Object> getRGBVector() {
        return Arrays.asList(Integer.toString(red), Integer.toString(green), Integer.toString(blue));
    }

    @NonNull
    public String toString() {
        return "Color{@" + id + ", " + name + ", " + getColorCode() + ", " + distance + "}";
    }

    public int hashCode() {
        int hash = id.hashCode();
        hash = hash * 31 + name.hashCode();
        hash = hash * 31 + ((((red * 257) + green) * 257) + blue) * 257;
        return hash * 31 + Double.hashCode(this.distance);
    }

    public boolean equals(@Nullable Object other) {
        if (other == this) { return true; }
        if (!(other instanceof NamedColor)) { return false; }
        final NamedColor c = (NamedColor) other;
        return Objects.equals(id, c.id)
            && Objects.equals(name, c.name)
            && (red == c.red)
            && (green == c.green)
            && (blue == c.blue)
            && Objects.equals(distance, c.distance);
    }
}

