package com.javadu.data

import com.javadu.data.database.entities.Lesson
import com.javadu.data.database.entities.Question

object DataInitializer {

    fun getInitialLessons(): List<Lesson> = listOf(
        Lesson(
            id = 1,
            title = "Основы Java",
            description = "Переменные, типы данных и операторы",
            theory = "Java — объектно-ориентированный язык программирования. Все программы состоят из классов и методов. Переменные используются для хранения данных. Основные типы: int (целое), double (дробное), boolean (логическое), String (строка).",
            codeExample = "int age = 25;\nString name = \"Alex\";\nboolean isActive = true;\ndouble price = 19.99;",
            xpReward = 20,
            order = 0
        ),
        Lesson(
            id = 2,
            title = "Условные операторы",
            description = "if, else и switch",
            theory = "Условные операторы позволяют выполнять разный код в зависимости от условия. if проверяет условие, else — альтернативный вариант, а switch позволяет выбирать из нескольких вариантов.",
            codeExample = "int score = 85;\nif (score >= 90) {\n    System.out.println(\"A\");\n} else if (score >= 80) {\n    System.out.println(\"B\");\n} else {\n    System.out.println(\"C\");\n}",
            xpReward = 20,
            order = 1
        ),
        Lesson(
            id = 3,
            title = "Циклы",
            description = "for, while и do-while",
            theory = "Циклы позволяют повторять код многократно. for используется, когда известно количество итераций. while — когда условие проверяется перед каждой итерацией. do-while гарантирует хотя бы одно выполнение.",
            codeExample = "for (int i = 0; i < 5; i++) {\n    System.out.println(i);\n}\n\nint n = 0;\nwhile (n < 3) {\n    System.out.println(n++);\n}",
            xpReward = 20,
            order = 2
        ),
        Lesson(
            id = 4,
            title = "Массивы",
            description = "Одномерные и многомерные массивы",
            theory = "Массив — это структура данных, хранящая элементы одного типа. В Java массивы имеют фиксированный размер. Для динамических коллекций используют ArrayList.",
            codeExample = "int[] numbers = {1, 2, 3, 4, 5};\nSystem.out.println(numbers[0]); // 1\n\nString[] names = new String[3];\nnames[0] = \"Alice\";",
            xpReward = 25,
            order = 3
        ),
        Lesson(
            id = 5,
            title = "ООП: Классы и Объекты",
            description = "Инкапсуляция, наследование, полиморфизм",
            theory = "ООП (Объектно-Ориентированное Программирование) — подход, при котором программа состоит из объектов. Класс — это шаблон, объект — экземпляр класса. Инкапсуляция скрывает внутренние детали, наследование позволяет переиспользовать код.",
            codeExample = "class Car {\n    private String brand;\n    \n    public Car(String brand) {\n        this.brand = brand;\n    }\n    \n    public void drive() {\n        System.out.println(brand + \" is driving\");\n    }\n}\n\nCar myCar = new Car(\"Toyota\");\nmyCar.drive();",
            xpReward = 30,
            order = 4
        ),
        Lesson(
            id = 6,
            title = "ООП: Интерфейсы",
            description = "Абстракция и реализация интерфейсов",
            theory = "Интерфейс в Java определяет контракт — набор методов, которые класс должен реализовать. Интерфейсы позволяют создавать слабосвязанный код и поддерживают множественное наследование. С Java 8 интерфейсы могут содержать default-методы.",
            codeExample = "interface Animal {\n    void makeSound();\n}\n\nclass Dog implements Animal {\n    public void makeSound() {\n        System.out.println(\"Woof!\");\n    }\n}",
            xpReward = 25,
            order = 5
        ),
        Lesson(
            id = 7,
            title = "API Testing",
            description = "REST API, HTTP методы и статусы",
            theory = "API Testing — тестирование интерфейсов программирования приложений. REST API использует HTTP методы: GET (получение), POST (создание), PUT (обновление), DELETE (удаление). Основные статусы: 200 OK, 201 Created, 400 Bad Request, 404 Not Found, 500 Internal Server Error.",
            codeExample = "// GET запрос\nGET /api/users/1\nResponse: 200 OK\n{\n    \"id\": 1,\n    \"name\": \"Alex\"\n}\n\n// POST запрос\nPOST /api/users\nBody: {\"name\": \"Bob\"}\nResponse: 201 Created",
            xpReward = 30,
            order = 6
        ),
        Lesson(
            id = 8,
            title = "UI Testing",
            description = "Selenium WebDriver и Page Object Model",
            theory = "UI Testing автоматизирует взаимодействие с пользовательским интерфейсом. Selenium WebDriver позволяет управлять браузером программно. Page Object Model (POM) — паттерн, при котором UI элементы выносятся в отдельный класс для лёгкости поддержки.",
            codeExample = "public class LoginPage {\n    @FindBy(id = \"username\")\n    private WebElement usernameField;\n    \n    public void login(String user) {\n        usernameField.sendKeys(user);\n        submitButton.click();\n    }\n}",
            xpReward = 35,
            order = 7
        )
    )

    fun getInitialQuestions(): List<Question> = listOf(
        // Урок 1: Основы Java
        Question(lessonId = 1, questionText = "Какой тип данных используется для хранения целых чисел?", correctAnswer = "int", option1 = "String", option2 = "int", option3 = "double"),
        Question(lessonId = 1, questionText = "Как объявить переменную с именем 'age', равной 25?", correctAnswer = "int age = 25;", option1 = "int age = 25;", option2 = "age = 25 int;", option3 = "var age = \"25\""),
        Question(lessonId = 1, questionText = "Какой тип для хранения текста?", correctAnswer = "String", option1 = "int", option2 = "boolean", option3 = "String"),
        Question(lessonId = 1, questionText = "Что выведет System.out.println(\"Hello\");?", correctAnswer = "Hello", option1 = "Hello", option2 = "\"Hello\"", option3 = "Ничего"),
        Question(lessonId = 1, questionText = "Какой тип для логических значений?", correctAnswer = "boolean", option1 = "int", option2 = "boolean", option3 = "String"),

        // Урок 2: Условные операторы
        Question(lessonId = 2, questionText = "Какой оператор используется для альтернативного условия?", correctAnswer = "else", option1 = "if", option2 = "else", option3 = "switch"),
        Question(lessonId = 2, questionText = "Какой оператор проверяет равенство?", correctAnswer = "==", option1 = "=", option2 = "==", option3 = "!="),
        Question(lessonId = 2, questionText = "Что выведет if (5 > 3) { System.out.println(\"Yes\"); }?", correctAnswer = "Yes", option1 = "No", option2 = "Yes", option3 = "Ошибка"),
        Question(lessonId = 2, questionText = "Какой оператор для выбора из многих вариантов?", correctAnswer = "switch", option1 = "if", option2 = "switch", option3 = "else"),
        Question(lessonId = 2, questionText = "if (x >= 10 && x <= 20) — что это проверяет?", correctAnswer = "x в диапазоне 10-20", option1 = "x = 10 или 20", option2 = "x в диапазоне 10-20", option3 = "x > 20"),

        // Урок 3: Циклы
        Question(lessonId = 3, questionText = "Какой цикл гарантирует хотя бы одно выполнение?", correctAnswer = "do-while", option1 = "for", option2 = "while", option3 = "do-while"),
        Question(lessonId = 3, questionText = "Сколько раз выполнится for (int i=0; i<3; i++)?", correctAnswer = "3", option1 = "2", option2 = "3", option3 = "4"),
        Question(lessonId = 3, questionText = "Какое ключевое слово прерывает цикл?", correctAnswer = "break", option1 = "continue", option2 = "break", option3 = "return"),
        Question(lessonId = 3, questionText = "Какое ключевое слово пропускает текущую итерацию?", correctAnswer = "continue", option1 = "break", option2 = "continue", option3 = "skip"),
        Question(lessonId = 3, questionText = "Чем отличается while от do-while?", correctAnswer = "Условие проверяется после", option1 = "Ничем", option2 = "Условие проверяется после", option3 = "Быстрее работает"),

        // Урок 4: Массивы
        Question(lessonId = 4, questionText = "Как получить длину массива arr?", correctAnswer = "arr.length", option1 = "arr.size()", option2 = "arr.length", option3 = "arr.count"),
        Question(lessonId = 4, questionText = "Первый индекс массива в Java?", correctAnswer = "0", option1 = "0", option2 = "1", option3 = "-1"),
        Question(lessonId = 4, questionText = "Какой тип для динамического массива?", correctAnswer = "ArrayList", option1 = "ArrayList", option2 = "LinkedList", option3 = "HashMap"),
        Question(lessonId = 4, questionText = "int[] a = new int[3]; Какое значение a[0]?", correctAnswer = "0", option1 = "0", option2 = "null", option3 = "Ошибка"),
        Question(lessonId = 4, questionText = "Можно ли изменить размер массива после создания?", correctAnswer = "Нет", option1 = "Да", option2 = "Нет", option3 = "Только в цикле"),

        // Урок 5: ООП Классы
        Question(lessonId = 5, questionText = "Что такое класс в Java?", correctAnswer = "Шаблон для объектов", option1 = "Метод", option2 = "Шаблон для объектов", option3 = "Переменная"),
        Question(lessonId = 5, questionText = "Как создать объект класса Car?", correctAnswer = "new Car()", option1 = "Car.create()", option2 = "new Car()", option3 = "Car()"),
        Question(lessonId = 5, questionText = "Какой модификатор скрывает поле извне?", correctAnswer = "private", option1 = "public", option2 = "private", option3 = "protected"),
        Question(lessonId = 5, questionText = "Что такое this?", correctAnswer = "Ссылка на текущий объект", option1 = "Новый объект", option2 = "Ссылка на текущий объект", option3 = "Статический метод"),
        Question(lessonId = 5, questionText = "Какой принцип ООП объединяет данные и методы?", correctAnswer = "Инкапсуляция", option1 = "Наследование", option2 = "Инкапсуляция", option3 = "Полиморфизм"),

        // Урок 6: ООП Интерфейсы
        Question(lessonId = 6, questionText = "Какое ключевое слово для реализации интерфейса?", correctAnswer = "implements", option1 = "extends", option2 = "implements", option3 = "interface"),
        Question(lessonId = 6, questionText = "Может ли класс реализовать несколько интерфейсов?", correctAnswer = "Да", option1 = "Да", option2 = "Нет", option3 = "Только 2"),
        Question(lessonId = 6, questionText = "Может ли интерфейс содержать поля?", correctAnswer = "Только static final", option1 = "Любые", option2 = "Только static final", option3 = "Нет"),
        Question(lessonId = 6, questionText = "С Java 8 интерфейсы могут содержать?", correctAnswer = "default методы", option1 = "private поля", option2 = "default методы", option3 = "конструкторы"),
        Question(lessonId = 6, questionText = "Чем интерфейс отличается от абстрактного класса?", correctAnswer = "Нет состояния (полей)", option1 = "Нет разницы", option2 = "Нет состояния (полей)", option3 = "Быстрее работает"),

        // Урок 7: API Testing
        Question(lessonId = 7, questionText = "Какой метод для получения данных?", correctAnswer = "GET", option1 = "POST", option2 = "GET", option3 = "DELETE"),
        Question(lessonId = 7, questionText = "Какой HTTP статус означает 'Не найдено'?", correctAnswer = "404", option1 = "500", option2 = "404", option3 = "200"),
        Question(lessonId = 7, questionText = "Какой метод создаёт новый ресурс?", correctAnswer = "POST", option1 = "GET", option2 = "POST", option3 = "PUT"),
        Question(lessonId = 7, questionText = "Что означает статус 500?", correctAnswer = "Internal Server Error", option1 = "Not Found", option2 = "Internal Server Error", option3 = "OK"),
        Question(lessonId = 7, questionText = "Какой формат чаще всего используется в REST API?", correctAnswer = "JSON", option1 = "XML", option2 = "HTML", option3 = "JSON"),

        // Урок 8: UI Testing
        Question(lessonId = 8, questionText = "Что такое Selenium WebDriver?", correctAnswer = "Инструмент для автоматизации браузера", option1 = "База данных", option2 = "Инструмент для автоматизации браузера", option3 = "Сборщик проектов"),
        Question(lessonId = 8, questionText = "Что такое Page Object Model?", correctAnswer = "Паттерн для UI элементов", option1 = "Тип теста", option2 = "Паттерн для UI элементов", option3 = "Фреймворк"),
        Question(lessonId = 8, questionText = "Какой метод кликает по элементу в Selenium?", correctAnswer = "click()", option1 = "press()", option2 = "click()", option3 = "tap()"),
        Question(lessonId = 8, questionText = "Что используется для поиска элементов?", correctAnswer = "@FindBy", option1 = "@FindBy", option2 = "@Search", option3 = "@Locate"),
        Question(lessonId = 8, questionText = "Какая команда для ввода текста в поле?", correctAnswer = "sendKeys()", option1 = "type()", option2 = "enter()", option3 = "sendKeys()")
    )
}
