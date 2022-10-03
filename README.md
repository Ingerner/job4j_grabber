# job4j_grabber
Проект Агрегатор вакансий.

Описание.
Система запускается по расписанию - раз в минуту. Период запуска указывается в настройках - app.properties.
Первый сайт будет career.habr.com. В нем есть раздел https://career.habr.com/vacancies/java_developer.
С ним будет идти работа. Программа должна считывать все вакансии относящиеся к Java и записывать их в базу.

0. Подключаем к проекту maven, checkstyle, jacoco.
1. Подключаем библиотеку Quartz (http://www.quartz-scheduler.org/). Настраиваем логгер. Подключаем базу данных Postgress.
2. Реализовать класс Grabber. Собираем разработанные части в приложение.