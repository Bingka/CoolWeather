# CoolWeather

http://m.weather.com.cn/data/101110101.html  
这个接口不更新了  
http://www.weather.com.cn/data/sk/101010100.html  
这个接口测试天气信息非常不准确，且信息量太少，但是全国省市县的接口还是可以用的。  

关于天气信息的两个接口(该app用的就是下面的json数据接口)  
1.json数据  
http://wthrcdn.etouch.cn/weather_mini?city=北京  
通过城市名字获得天气数据  
http://wthrcdn.etouch.cn/weather_mini?citykey=101010100  
通过城市id获得天气数据  
2.xml数据  
http://wthrcdn.etouch.cn/WeatherApi?citykey=101010100  
通过城市id获得天气数据  
当错误时会有<error>节点  
http://wthrcdn.etouch.cn/WeatherApi?city=北京  
通过城市名字获得天气数据  


以上信息来自下面的博客：http://blog.csdn.net/fancylovejava/article/details/26102635
