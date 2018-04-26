var request = require('request'),
    cheerio = require('cheerio'),
    fs = require("fs"),
    browserSync = require("browser-sync").create();
    schedule = require("node-schedule");

var arryData = [],
      pageNum = 1,
   maxPageNum = 6;


// Callback of the simplified HTTP request client
function reqCallback(err, response, body) {
    if (!err && response.statusCode == 200) {
        // 解析数据
        var $ = cheerio.load(body),
            $tr = $('.BOC_main tr'),
            $child = '', arryTmp = [],
            i = 1, len = $tr.length - 1;

        for (i; i < len; i++) {
            $child = $tr.eq(i).children();

            arryTmp.push(Number($child.eq(1).text())) // 现汇买入
            arryTmp.push(Number($child.eq(2).text())) // 现钞买入
            arryTmp.push(Number($child.eq(3).text())) // 现汇卖出
            arryTmp.push(Number($child.eq(4).text())) // 现钞卖出
            arryTmp.push($child.eq(7).text()) // 发布时间

            arryData.push(arryTmp)
            arryTmp = []
        }

        fetchInfo()
    }
}


// 请求数据
function fetchInfo() {
    if (pageNum <= maxPageNum) {
        console.log('读取第'+ pageNum +'页数据...');
        request({
            url: 'http://srh.bankofchina.com/search/whpj/search.jsp',
            method: 'POST',
            form: {
                pjname: 1316,
                page: pageNum++
            }
        }, reqCallback)
    } else {
        // 保存数据
        fs.writeFile('./app/data.json', JSON.stringify(arryData), function(err) {
            if (err) throw err;
            console.log('数据保存成功');
        })
        // 前台展示数据
        // browserSync.init({
        //     server: "./app",
        //     browser: "google chrome"
        // });
        return
    }
}

console.log('开始抓取数据...');
fetchInfo();
    // 定时执行抓取任务（5min 刷一次页面数据）
    var rule    = new schedule.RecurrenceRule();
    var times    = [1,6,11,16,21,26,31,36,41,46,51,56];
    rule.minute  = times;
    schedule.scheduleJob(rule, function(){
    fetchInfo();
    });