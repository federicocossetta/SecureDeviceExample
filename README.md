# SecureDeviceExample

Simple implementation of [SecureDevice](https://github.com/federicocossetta/SecureDevice) lib

This application is using Retrofit as Network provider, and shows a list of github users 
connected to device.

It does not shows any data about device, but run only if device is trusted. To access device 
data collected, use


```
securityChecker.getAnalysisResult()
```

