This is a demo project for the ElasticSearch

ElasticSearch Version: 6.3.2
Guide: https://www.elastic.co/guide/en/elasticsearch/reference/6.3/index.html
IK Version: 6.3.2

--抓取数据(Ubuntu)
# wget -o /tmp/wget.log -P /root/data --no-parent --no-verbose -m -D news.cctv.com -N --convert-links --random-wait -A html,HMTL,shtml,SHTML http://news.cctv.com
# tail -f /tmp/wget.log

--ElasticSearch GUI
# cd C:\Tools\Elastic\ElasticSearch
# ElasticHD.exe -p 127.0.0.1:9800
