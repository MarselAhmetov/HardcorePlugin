# Проект HardcorePlugin

Добро пожаловать в HardcorePlugin! Этот проект представляет собой плагин для сервера Minecraft. Плагин позволяет возродить игроков на хардкорном сервере за плату в виде некоторого списка предметов.

## Запуск проекта

Для успешного запуска проекта, выполните следующие шаги:

1. Соберите проект с использованием Maven:

    ```bash
    mvn clean package
    ```

2. Положите полученный JAR файл в директорию "plugins" внутри папки сервера.

3. Запустите сервер!

## Фичи

- Команда (доступна только для op) `/dev` для получения предметов, необходимых для возрождения всех игроков.
- Команда (доступна только для op) `/stick` для получения палки для возрождения игроков.
- При нажатии правой кнопки мыши на палку для возрождения открывается инвентарь с головами игроков, доступных для возрождения.
- При наведении на голову игрока, показывается список материалов, необходимых для его возрождения.
- При нажатии левой кнопки мыши на голову игрока, при условии, что материалы есть в инвентаре нажимающего, предметы списываются, и игрок возрождается.
- Данные об игроках, доступных для возрождения, хранятся в `plugins/hardcore-plugin/respawnable_players.txt` в формате `NICKNAME, MATERIAL:1, MATERIAL_TWO:10, MATERIAL_THREE:5, MATERIAL_FOUR:4` и удаляются после добавления в список на возрождение.
- Ники игроков, которые должны быть возрождены, хранятся в `plugins/hardcore-plugin/players_to_revive.txt` и удаляются после возрождения.
- Игрок может быть выкуплен, даже когда он оффлайн; в этом случае он будет возрожден при следующем входе в игру.
- Игрок становится доступен для возрождения только, когда он нажимает кнопку "возродиться" после смерти.
- Предметы для возрождения игрока случайно выбираются из списка материалов, необходимое количество каждого предмета также вычисляется случайно в пределах от `lowerThreshold` до `upperThreshold`.

## Настройка

### Добавление Списка Предметов
При первом запуске сервера с плагином, будет создана папка materials со списками предметов в виде JSON файлов.

Для добавления списка предметов, выполните следующие шаги:

1. Создайте JSON файл с вашим списком предметов. Пример формата данных:

    ```json
    [
        {
            "material": "CARROT",
            "lowerThreshold": 10,
            "upperThreshold": 15,
            "tier": "OVER_WORLD"
        },
        {
            "material": "DIAMOND",
            "lowerThreshold": 5,
            "upperThreshold": 20,
            "tier": "END"
        }
    ]
    ```
    - material - Название предмета из `Material.class`.
    - lowerThreshold - Минимальное количество.
    - upperThreshold - Максимальное количество.
    - tier - Уровень достижений игрока
        - END - Игрок посетил Мир Энда.
        - NETHER - Игрок посетил Мир Незера.
        - OVER_WORLD - Игрок еще не посетил ни один из миров.

2. Положите созданный JSON файл в папку "materials" внутри директории проекта.

3. Перезапустите сервер для применения изменений.

Теперь ваш проект будет использовать новый список предметов с указанными параметрами.
Из каждого списка предметов случайно выбирается один.

## Требования

Убедитесь, что у вас установлены следующие компоненты:

- [Java](https://www.oracle.com/java/technologies/javase-downloads.html) версии 8 или выше.
- [Maven](https://maven.apache.org/download.cgi).

## Вклад в проект

Если у вас есть идеи по улучшению проекта, не стесняйтесь внести свой вклад! Мы открыты для новых идей и предложений.

## Связь

Если у вас есть вопросы или предложения, не стесняйтесь связаться с нами.