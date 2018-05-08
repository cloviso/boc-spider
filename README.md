# boc-spider

Node.js, Robot/Spider, ECharts

运行方法：

1. `npm install`，安装所需的 node 模块
2. `node index.js`，抓取中国银行外汇牌价表欧元-人民币实时汇率，当数据在指定json文件中存储完毕后，自动打开前端图表展示页面

加了个定时任务 五分钟抽取一次美元汇率。

[Demo](https://vivi-wu.github.io/boc-spider/app/)





我使用Java 的 webmagic 并利用 spring boot 框架抽取了美元汇率数据。代码封装了下，也可以爬其他汇率，并持久化到数据库了。

开发环境是 jdk1.8 + mysql5.7 + eclipse.
