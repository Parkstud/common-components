package test.day1.demo1.group2


import spock.lang.Specification

/**
 * 测试校验器
 * @author parkstud@qq.com 2020-09-15
 */
class StudentTest extends Specification {

    def "test no validate class"() {
        given: "初始对象"
        def result = new ValidationResult<Student>()
        when:
        ValidUtils.valid(null, result, student)
        then:
        println result
        result.error == error
        where:
        student                            || error
        new Student(
                age: 18,
                birthday: new Date(100L),
                name: "陈苗",
                phone: "18280406477",
                price: 10.00,
                score: 160,
                email: "380202037@qq.com") || true
        new Student(
                score: 150
        )                                  || false
    }

    def "test validate1 class"() {
        expect:
        def result = new ValidationResult<Student>()
        ValidUtils.valid(null, result, student, Student.Validate1.class)
        println result
        result.error == mytestResult.error
        where:
        student                     || mytestResult
        new Student(
                age: 18,
                birthday: new Date(100L),
                name: "陈苗",
                phone: "",
                price: -1,
                score: -1,
                email: "380202037") || new ValidationResult(error: false)
        new Student(
                age: 2000,
                birthday: new Date(new Date().getTime() + 100000000L),
                name: "",
                phone: "200000000000000000000000",
                price: -1,
                score: -1,
                email: "380202037") || new ValidationResult(error: true)
    }

    def "test validate2 class"() {
        expect:
        def result = new ValidationResult<Student>()
        ValidUtils.valid(null, result, student, Student.Validate2.class)
        println result
        result.error == mytestResult.error
        where:
        student                            || mytestResult
        new Student(
                age: 3000,
                birthday: new Date(100L),
                name: "陈苗",
                phone: "18280406477",
                price: 11,
                score: -1,
                email: "380202037@qq.com") || new ValidationResult(error: false)
        new Student(
                age: 30,
                birthday: new Date(new Date().getTime()),
                name: "aa",
                phone: "30000000000000000",
                price: -1,
                score: 11,
                email: "380202037")        || new ValidationResult(error: true)
    }

    def "test validate1 and validate2 class"() {
        expect:
        def result = new ValidationResult<Student>()
        ValidUtils.valid(null, result, student, Student.Validate2.class, Student.Validate1.class)
        println result
        result.error == mytestResult.error
        where:
        student                            || mytestResult
        new Student(
                age: 3000,
                birthday: new Date(100000L + new Date().getTime()),
                name: "",
                phone: "18280406477",
                price: -1,
                score: 2000,
                email: "380202037")        || new ValidationResult(error: true)
        new Student(
                age: 30,
                birthday: new Date(new Date().getTime()),
                name: "aa",
                phone: "300000000000000000000000000000",
                price: 1,
                score: 11,
                email: "380202037@qq.com") || new ValidationResult(error: true)
    }
}