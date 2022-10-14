# java-kanban
## Яндекс-практикум
### Спринт 8

## Техническое задание
Возвращаемся к работе над менеджером задач. Основная логика приложения реализована, теперь можно сделать для него API.
Вам предстоит настроить доступ к методам менеджера через HTTP-запросы.

### Прорабатываем логику API
Вам нужно реализовать API, где эндпоинты будут соответствовать вызовам базовых методов интерфейса TaskManager.
Соответствие эндпоинтов и методов называется маппингом. Вот как это должно будет выглядеть.

Сначала добавьте в проект библиотеку Gson для работы с JSON. Далее создайте класс HttpTaskServer, который будет слушать
порт 8080 и принимать запросы. Добавьте в него реализацию FileBackedTaskManager, которую можно получить из утилитного
класса Managers. После этого можно реализовать маппинг запросов на методы интерфейса TaskManager.

API должен работать так, чтобы все запросы по пути /tasks/<ресурсы> приходили в интерфейс TaskManager. Путь для обычных
задач — /tasks/task, для подзадач — /tasks/subtask, для эпиков — /tasks/epic. Получить все задачи сразу можно будет по
пути /tasks/, а получить историю задач по пути /tasks/history.

Для получения данных должны быть GET-запросы. Для создания и изменения — POST-запросы. Для удаления — DELETE-запросы.
Задачи передаются в теле запроса в формате JSON. Идентификатор (id) задачи следует передавать параметром запроса (через
вопросительный знак).

В результате для каждого метода интерфейса TaskManager должен быть создан отдельный эндпоинт, который можно будет
вызвать по HTTP.

### Как проверить эндпоинты
Проверить API можно несколькими способами.
Через Insomnia.
С помощью плагина для браузера, к примеру, RESTED, Postman, RESTClient или других. Выбрать и скачать подходящий можно
по ссылке.
В IDEA через шаблоны HTTP-запросов — scratch file. Нажмите комбинацию CTRL+SHIFT+ALT+Insert и выберите HTTP Request.

### Доделываем HTTP-сервер для хранения задач
Сейчас задачи хранятся в файлах. Нужно перенести их на сервер. Для этого напишите HTTP-клиент. С его помощью мы
переместим хранение состояния менеджера из файлов на отдельный сервер.

Шаблон сервера находится в репозитории — https://github.com/praktikum-java/java-core-bighw-kvserver. Склонируйте его и
перенесите в проект класс KVServer. В классе Main посмотрите пример, как запустить сервер правильно. Добавьте такой же
код в свой проект. В примере сервер запускается на порту 8078, если нужно, это можно изменить.

Вам нужно дописать реализацию запроса load() — это метод, который отвечает за получение данных. Доделайте логику работы
сервера по комментариям (комментарии затем можно убрать). После этого запустите сервер и проверьте, что получение
значения по ключу работает. Для начальной отладки можно делать запросы без авторизации, используя код DEBUG.

### Пишем HTTP-клиент
Для работы с хранилищем вам потребуется HTTP-клиент, который будет делегировать вызовы методов в HTTP-запросы. Создайте
класс KVTaskClient. Его будет использовать класс HTTPTaskManager, который мы скоро напишем.

При создании KVTaskClient учтите следующее:
Конструктор принимает URL к серверу хранилища и регистрируется. При регистрации выдаётся токен (API_TOKEN), который
нужен при работе с сервером.
Метод void put(String key, String json) должен сохранять состояние менеджера задач через запрос
POST /save/<ключ>?API_TOKEN=.
Метод String load(String key) должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_TOKEN=.

Далее проверьте код клиента в main. Для этого запустите KVServer, создайте экземпляр KVTaskClient. Затем сохраните
значение под разными ключами и проверьте, что при запросе возвращаются нужные данные. Удостоверьтесь, что если изменить
значение, то при повторном вызове вернётся уже не старое, а новое.

[### Новая реализация менеджера задач
Теперь можно создать новую реализацию интерфейса TaskManager — класс HTTPTaskManager. Он будет наследовать от
FileBackedTasksManager.

Конструктор HTTPTaskManager должен будет вместо имени файла принимать URL к серверу KVServer. Также HTTPTaskManager
создаёт KVTaskClient, из которого можно получить исходное состояние менеджера. Вам нужно заменить вызовы сохранения
состояния в файлах на вызов клиента.

В конце обновите статический метод getDefault() в утилитарном классе Managers, чтобы он возвращал HTTPTaskManager.]()

### Тестирование
Код проверки в Main.main перестал работать. Это произошло, потому что Managers.getDefault() теперь возвращает новую
реализацию менеджера задач, а она не может работать без запуска сервера. Вам нужно это исправить.

Добавьте запуск KVServer в Main.main и перезапустите пример использования менеджера. Убедитесь, что всё работает и
состояние задач теперь хранится на сервере.

Теперь можно добавить тесты для HTTPTaskManager аналогично тому как сделали для FileBackedTasksManager , отличие только,
вместо проверки восстановления состояния менеджера из файла, данные будут восстанавливаться с KVServer сервера.

Напишите тесты для каждого эндпоинта HTTPTaskServer. Чтобы каждый раз не добавлять запуск KVServer и HTTPTaskServer
серверов, можно реализовать в классах с тестами отдельный метод. Пометьте его аннотацией @BeforeAll — если
предполагается запуск серверов для всех тестов или аннотацией @BeforeEach — если для каждого теста требуется отдельный
запуск.

Ура! Теперь наше приложение доступно по HTTP и умеет хранить своё состояние на отдельном сервере! Вы молодец!