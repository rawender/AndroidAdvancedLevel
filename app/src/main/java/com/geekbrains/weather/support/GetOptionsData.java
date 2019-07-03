package com.geekbrains.weather.support;

public interface GetOptionsData {
    void getCurrentIndex(int index,
                         boolean airHumidityFlag,
                         boolean windSpeedFlag,
                         boolean pressureFlag);
}
