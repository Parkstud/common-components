package test.day1.demo1.group2


import java.time.Duration
//import org.apache.poi.hssf.usermodel.HSSFCell
//import org.apache.poi.hssf.usermodel.HSSFRow
//import org.apache.poi.hssf.usermodel.HSSFSheet
//import org.apache.poi.hssf.usermodel.HSSFWorkbook
//import org.o2.ext.cms.domain.entity.Component

import java.time.Instant
/**
 *
 * groovy语言学习
 * @author miao.chen01@hand-china.com 2020-07-31
 */

/*
 闭包
*/

//def static funcWithClosure(int num, final Closure closure) {
//    (2..num).collect {
//        closure(it)
//    }
//}
//
//println funcWithClosure(5, { x -> x * x });
//println funcWithClosure(5, { x -> x + 2 })
//
//def static addByClosure(init) {
//    def addInner = {
//        inc ->
//            init += inc
//            init
//    }
//    return addInner
//}
//
//def addClosure = addByClosure(0)
//println "one call :${addClosure(5)}"
//println "one call :${addClosure(5)}"
//
//def sumPower = {
//    power, int num ->
//        def sum = 0
//        1.upto(num) {
//            sum += Math.pow(it, power)
//        }
//        sum
//}
//def sumPower_2 = sumPower.curry(2)
//
//println "1^2 + 2^2 + 3^2 = ${sumPower_2(3)}"

/**
 * 计算时间
 * @param closure 闭包
 * @return 时间
 */
def computeTime(final Closure closure) {
    def now = Instant.now()
    closure()
    def end = Instant.now();
    println Duration.between(now, end).toMillis();
}


//computeTime {
//    List<Person> results = new ArrayList<Person>(2048)
//    Person person = new Person()
//    for (int i = 0; i < 1000; i++) {
//        results.add(person.clone())
//    }
//}
/**
 * 文件操作
 */
//new File("E:\\company\\newsvn\\8-产品测试\\8.3-压力测试\\1.2.0\\02.压测脚本\\压测脚本\\jmeter配置\\user.properties").eachLine {
//    line-> println "${line}"
//}

//def file = new File("E:\\company\\newsvn\\8-产品测试\\8.3-压力测试\\1.2.0\\02.压测脚本\\压测脚本\\jmeter配置\\user.properties")
//println file.text

//new File("C:\\Users\\chen\\Desktop\\user.text").withWriter("utf-8") {
//    writer->writer.writeLine('test')
//}

/**
 * 数据库
 */

//def conn = Sql.newInstance("jdbc:mysql://db.o2dev.org:3306/o2_extension?useUnicode=true&characterEncoding=utf-8&useSSL=false&useTimezone=true&serverTimezone=GMT%2B8",
//        "hone_dev",
//        "hone_dev2020",
//        "com.mysql.cj.jdbc.Driver")
//conn.eachRow('select * from o2cms_component') {
//    row ->
//        def com = new Component(tenantId: row.tenant_id,componentId: row.component_id,componentCode: row.component_code)
//        println com
//}
//
////创建Excel工作簿
//HSSFWorkbook hssfWorkbook=new HSSFWorkbook();
//HSSFSheet excelSheet = hssfWorkbook.createSheet("测试")
////设置默认列宽
//excelSheet.setDefaultColumnWidth(50)
////行
//HSSFRow hssfRow = null
////列
//HSSFCell hssfCell = nul







