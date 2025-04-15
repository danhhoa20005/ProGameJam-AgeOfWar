package com.ageofwar.models;

public enum Era {
    STONE,
    MEDIEVAL,
    INDUSTRIAL, // Renamed from Cận Đại for clarity
    MODERN,
    FUTURE;

    public static Era getNextEra(Era currentEra) {
        switch (currentEra) {
            case STONE: return MEDIEVAL;
            case MEDIEVAL: return INDUSTRIAL;
            case INDUSTRIAL: return MODERN;
            case MODERN: return FUTURE;
            case FUTURE: return null; // Already max era
            default: return null;
        }
    }
}
