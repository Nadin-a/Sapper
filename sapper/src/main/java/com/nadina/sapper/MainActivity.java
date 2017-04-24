package com.nadina.sapper;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * MIT License
 * <p>
 * Copyright (c) 2017 Nadia
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

@TargetApi(21)
public class MainActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener {

    /**
     * Начальное меню
     */
    private LinearLayout StartMenu;
    /**
     * Главный Layout
     */
    private LinearLayout mainLL;
    /**
     * Правила
     */
    private LinearLayout rul; //правила
    /**
     * Layout с победой
     */
    private FrameLayout Victory;
    /**
     * Текст "победа"
     */
    private TextView victory_text;
    /**
     * Информация
     */
    private TextView info_view;
    /**
     * Grid Layout
     */
    private GridLayout mainGL;
    /**
     * Анимация
     */
    private AnimatorSet textSizeAnimatorSet;


    /**
     * Игровые значения
     */
    /**
     * Для проверки текущей версии АПИ
     */
    int currentapiVersion;
    /**
     * Размер поля (определяется при нажатии кнопки)
     */
    private static int SIZE_FIELD = 0;
    /**
     * массив кнопок
     */
    private Button[][] buttons;
    /**
     * Количество мин
     */
    public static int count_of_mines = 0;
    /**
     * Количество флагов
     */
    public static int count_of_flags;
    /**
     * Очки победы
     */
    int count_of_victory = 0;
    /**
     * Признак окончания игры
     */
    private boolean GM = false;
    /**
     * Состоялась ли победа
     */
    public boolean was_victory = false;
    /**
     * значения поля мин
     */
    static Cell[][] map;
    /**
     * Пропуск заставки победы
     */
    boolean skip = false;


    /**
     * Константы
     */

    public static final int ON_CLICK = 0;
    public static final int ON_LONG_CLICK = 1;
    public static final int FIND_MINE = 1;
    public static final int NOT_MINE = 0;
    public static final int IS_MINE = -1;
    public static final int COUNT_MINES_MIN = 3;
    public static final int EXPLOSION = -2;
    public static final int COUNT_MINES_MAX = 5;
    public static final int SIZE_OF_FIELD_MIN = 7;
    public static final int SIZE_OF_FIELD_MAX = 9;
    public static final int SIZE_OF_CELL = 10;


    /**
     * Работа со звуком
     */
    private SoundPool sp;
    private int soundIdExplode; //ID звук. файла взрыва
    private int soundIdMusic; //ID звук. файла музыки
    private int soundIdVictory; //ID звук. файла победы
    private int streamIdExplode; //ID звук. потока
    private int streamIdMusic; //ID звук. потока
    private int streamIdVictory; //ID звук. потока
    private boolean isIdExplodeLoad; //Признак, что explode.mp3  загружен
    private boolean isIdMusicLoad; //Признак, что music.mp3  загружен
    private boolean isIdVictoryLoad; //Признак, что Victory.mp3  загружен
    private boolean isMusicPause = false; // пауза для музыки
    boolean CheckMusic = false; // проверка включения музыки на фоне

    public static final int CELL_SIZE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = this.getLayoutInflater();
        this.mainLL = (LinearLayout) inflater.inflate(R.layout.activity_main, null, false);

        this.StartMenu = (LinearLayout) inflater.inflate(R.layout.my_start_menu, null, false);
        this.Victory = (FrameLayout) inflater.inflate(R.layout.victory, null, false);
        this.rul = (LinearLayout) inflater.inflate(R.layout.rules, null, false);
        View view = inflater.inflate(R.layout.victory, Victory, false);

        //анимация для победы
        this.victory_text = (TextView) view.findViewById(R.id.win);
        Victory.addView(view);

        this.textSizeAnimatorSet = (AnimatorSet)
                AnimatorInflater.loadAnimator(this, R.animator.size_animator);


        ArrayList<Animator> arrL = this.textSizeAnimatorSet.getChildAnimations();
        ValueAnimator va = (ValueAnimator) arrL.get(0);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float txtSz = (float) animation.getAnimatedValue();
                victory_text.setTextSize(TypedValue.COMPLEX_UNIT_PT, txtSz);
            }
        });

        currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {

            //------- создание объекта SoundPool----------------------
            SoundPool.Builder builder = new SoundPool.Builder(); // создаём объект построитель
            builder.setAudioAttributes(
                    new AudioAttributes.Builder().
                            setUsage(AudioAttributes.USAGE_MEDIA).
                            setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
            builder.setMaxStreams(5);
            this.sp = builder.build();
            //----- назначаем слушателя на загрузку файлов-------
            sp.setOnLoadCompleteListener(this);

            //----- Загружаем файлы ----------------------------------
            this.soundIdExplode = sp.load(this, R.raw.sound, 1);
            this.soundIdMusic = sp.load(this, R.raw.music, 1);
            this.soundIdVictory = sp.load(this, R.raw.victory_sound, 1);


        }

        lockScreenOrientation();

        setContentView(this.StartMenu);
    }

    /**
     * Для музыки
     */
    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleID, int status) {

        if (status != 0) return;

        if (sampleID == this.soundIdExplode) {
            this.isIdExplodeLoad = true;
        } else if (sampleID == this.soundIdMusic) {
            this.isIdMusicLoad = true;
        } else if (sampleID == this.soundIdVictory) {
            this.isIdVictoryLoad = true;
        }
    }

    /**
     * Для паузы в музыке
     */

    private void MusicPause() {
        if (this.isMusicPause) {
            //----- Возобновляем воспроизведение
            this.sp.resume(this.streamIdMusic);

        } else {
            ///----- Ставим на паузу
            this.sp.pause(this.streamIdMusic);
        }
        this.isMusicPause = !this.isMusicPause;
    }

    public void btnClickPause(View v) {
        if (v.getId() == R.id.music) {
            if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
                if (CheckMusic == false) {
                    this.streamIdMusic = this.sp.play(this.soundIdMusic, 1, 1, 1, -1, 1);
                    CheckMusic = true;

                } else {
                    MusicPause();
                }
            }
        }
    }

    /*
    Запрещаю поворот
     */
    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    /**
     * Рисуется поле.
     */
    public void My_Game_Field() {


        GM = false;
        was_victory = false;
        count_of_mines = 0;
        count_of_victory = 0;
        info_view = new TextView(this);
        info_view.setPadding(
                (int) this.getResources().getDimension(R.dimen.activity_horizontal_margin_flags),
                (int) this.getResources().getDimension(R.dimen.activity_vertical_margin_flags),
                (int) this.getResources().getDimension(R.dimen.activity_horizontal_margin_flags),
                (int) this.getResources().getDimension(R.dimen.activity_vertical_margin_flags));
        info_view.setTextColor(Color.RED);


        this.mainGL = new GridLayout(this);


        if (SIZE_FIELD == SIZE_OF_FIELD_MAX) {
            this.mainGL.setPadding(
                    (int) this.getResources().getDimension(R.dimen.activity_horizontal_margin),
                    (int) this.getResources().getDimension(R.dimen.activity_vertical_margin),
                    (int) this.getResources().getDimension(R.dimen.activity_horizontal_margin),
                    (int) this.getResources().getDimension(R.dimen.activity_vertical_margin));
        } else {
            this.mainGL.setPadding(
                    (int) this.getResources().getDimension(R.dimen.activity_horizontal_margin_min),
                    (int) this.getResources().getDimension(R.dimen.activity_vertical_margin_min),
                    (int) this.getResources().getDimension(R.dimen.activity_horizontal_margin_min),
                    (int) this.getResources().getDimension(R.dimen.activity_vertical_margin_min));
        }


        this.mainGL.setRowCount(SIZE_FIELD); // строки
        this.mainGL.setColumnCount(SIZE_FIELD); // столбцы
        TableLayout.LayoutParams mainTLParams = new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mainGL.setLayoutParams(mainTLParams);
        this.mainGL.setOrientation(GridLayout.VERTICAL);
        this.mainGL.setBackgroundResource(R.drawable.background);


        buttons = new Button[SIZE_FIELD][SIZE_FIELD];
        if (SIZE_FIELD == SIZE_OF_FIELD_MIN) {
            do {
                map = generate();
            } while (count_of_mines != COUNT_MINES_MIN);
        } else {
            do {
                map = generate();
            } while (count_of_mines != COUNT_MINES_MAX);
        }
        count_of_flags = count_of_mines; // сколько мин, столько и флагов

        info_view.setText(String.valueOf(count_of_flags));

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons.length; j++) {
                buttons[i][j] = new Button(this);
                buttons[i][j].setMinimumHeight(SIZE_OF_CELL);
                buttons[i][j].setMinimumWidth(SIZE_OF_CELL);
                buttons[i][j].setWidth(SIZE_OF_CELL);
                buttons[i][j].setHeight(SIZE_OF_CELL);
                buttons[i][j].setBackgroundResource(R.drawable.hidden);

                map[i][j].setX(i);
                map[i][j].setY(j);
                buttons[i][j].setTag(map[i][j]);

                mainGL.addView(buttons[i][j]);


            }
        }

        mainLL.addView(mainGL);

        mainGL.addView(info_view);
        Touch_Buttons();


    }


    /**
     * Метод, что обрабатывает события нажатия на кнопку
     */
    public void Touch_Buttons() {

        /**
         * Выставляем флаги
         */
        final View.OnLongClickListener oclBtnLong = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (GM == true) {
                    mainLL.removeAllViews();
                    setContentView(StartMenu);
                }
                for (int i = 0; i < buttons.length; i++) {
                    for (int j = 0; j < buttons.length; j++) {

                        if ((Cell) v.getTag() == map[i][j]) { // Определить кординаты нажатия
                            if (count_of_flags != 0) { // если флаги есть, отправляем нажатие
                                Input(map[i][j], ON_LONG_CLICK, NOT_MINE);
                            }
                            do {
                                if ((!map[i][j].getMarked()) && (count_of_flags > 0)) { // если флаг не проставлен и они есть, ставмм один
                                    buttons[i][j].setBackgroundResource(R.drawable.state_for_flag);
                                    count_of_flags--;
                                    map[i][j].setMarked(!map[i][j].getMarked());
                                } else if ((map[i][j].getMarked()) && (count_of_flags >= 0)) { //если флаг есть, убираем его с клетки и возвращаем себе
                                    if (map[i][j].getHidden()) {
                                        buttons[i][j].setBackgroundResource(R.drawable.hidden);
                                    } else {
                                        buttons[i][j].setBackgroundResource(R.drawable.empty);
                                    }
                                    count_of_flags++;
                                    map[i][j].setMarked(!map[i][j].getMarked()); //меняем состояние
                                }
                            } while (count_of_flags > count_of_mines);
                        }
                    }
                }
                info_view.setText(String.valueOf(count_of_flags));
                return true;
            }

        };


        /**
         * Обработик обычного нажатия
         */
        final View.OnClickListener oclBtnOk = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GM) {
                    mainLL.removeAllViews();
                    setContentView(StartMenu);
                }
                for (int i = 0; i < buttons.length; i++) {
                    for (int j = 0; j < buttons.length; j++) {
                        if ((Cell) v.getTag() == map[i][j]) { // определяем координаты нажатия
                            if (!map[i][j].getMarked()) { //если нет флага
                                if (map[i][j].getState() == MainActivity.IS_MINE) { // если попали на мину
                                    Input(map[i][j], ON_CLICK, FIND_MINE);
                                } else {
                                    Input(map[i][j], ON_CLICK, NOT_MINE);
                                }

                            }
                        }

                        buttons[i][j].setBackgroundResource(map[i][j].getSprite(map[i][j])); // перерисовка

                        /**
                         * Определяем побелу
                         */
                        if (!map[i][j].getVictory()) //если клетка не была открыта
                        {
                            if ((!map[i][j].getHidden()) && (map[i][j].getState() != IS_MINE)) { //если была открыта и без мины
                                count_of_victory++;
                                map[i][j].setVictory(true); // ставим, что была открыта
                            }
                        }

                        //если всё кроме мин открыто
                        if ((count_of_victory == (SIZE_FIELD * SIZE_FIELD) - count_of_mines) && (!GM)) {
                            if ((map[i][j].getState() == IS_MINE) || (map[i][j].getState() == MainActivity.EXPLOSION)) {
                                buttons[i][j].setBackgroundResource(R.drawable.mine);
                            }

                            buttons[i][j].setClickable(false);
                            map[i][j].show(map[i][j]);
                            buttons[i][j].setBackgroundResource(map[i][j].getSprite(map[i][j]));
                            was_victory = true;
                            VictoryEffects();

                        }
                    }
                }
            }
        };

        /**
         * Назначаем слушателей
         */
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons.length; j++) {
                buttons[i][j].setOnLongClickListener(oclBtnLong);
                buttons[i][j].setOnClickListener(oclBtnOk);
            }
        }

    }


    public void VictoryEffects() {
        if (was_victory == true) {
            if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
                streamIdVictory = sp.play(soundIdVictory, 1, 1, 1, 0, 1);
            }
            new CountDownTimer(800, 1000) {

                @Override
                public void onTick(long miliseconds) {
                }

                @Override
                public void onFinish() {
                    setContentView(Victory);
                    textSizeAnimatorSet.start();
                }
            }.start();

            if (skip == false) {
                new CountDownTimer(3500, 1000) {

                    @Override
                    public void onTick(long miliseconds) {
                    }

                    @Override
                    public void onFinish() {
                        mainLL.removeAllViews();
                        setContentView(StartMenu);
                    }
                }.start();
            }
        }
    }


    /**
     * Метод, что генерирует мины и счетчики
     *
     * @return map состояние ячейки
     */
    public static Cell[][] generate() {
        {
            Random rnd = new Random();

            count_of_mines = 0;

            ///Карта, которую мы вернём
            Cell[][] map = new Cell[SIZE_FIELD][SIZE_FIELD];

            ///Матрица с пометками, указывается кол-во мин рядом с каждой клеткой
            int[][] counts = new int[SIZE_FIELD][SIZE_FIELD];


            for (int x = 0; x < SIZE_FIELD; x++) {
                for (int y = 0; y < SIZE_FIELD; y++) {
                    boolean isMine = rnd.nextInt(100) < 15;

                    if (isMine) {

                        map[x][y] = new Cell(x * CELL_SIZE, y * CELL_SIZE, -1);


                        for (int i = -1; i < 2; i++) {
                            for (int j = -1; j < 2; j++) {
                                try {
                                    if (map[x + i][y + j] == null) {
                                        ///Если кдетки там ещё нет, записываем сведение
                                        ///о мине в матрицу
                                        counts[x + i][y + j] += 1;
                                    } else {
                                        ///Если есть, говорим ей о появлении мины
                                        map[x + i][y + j].incNearMines();
                                    }

                                } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                                    Log.d("====", e.getMessage());
                                }
                            }
                        }
                        count_of_mines++;

                    } else {
                        ///Если была сгенерирована обычная клетка, создаём её, со
                        ///state равным значению из матрицы
                        map[x][y] = new Cell(x * CELL_SIZE, y * CELL_SIZE, counts[x][y]);
                    }
                }
            }

            return map;
        }
    }


    /**
     * Нажатие на кнопку в главном меню
     */
    public void btnClick(View v) {
        switch (v.getId()) {
            case R.id.min: {
                SIZE_FIELD = SIZE_OF_FIELD_MIN;
                My_Game_Field();
                setContentView(mainLL);
                break;
            }
            case R.id.max: {
                SIZE_FIELD = SIZE_OF_FIELD_MAX;
                My_Game_Field();
                setContentView(mainLL);
                break;
            }
            case R.id.rules: {
                setContentView(rul);
                break;
            }
        }

    }


    public void btnClickWin(View v) {
        if (v.getId() == R.id.victory_back) {
            skip = true;
            mainLL.removeAllViews();
            setContentView(this.StartMenu);
        }
        if (v.getId() == R.id.RulesId) {
            setContentView(this.StartMenu);
        }
    }

    /**
     * Проверка на конец игры
     *
     * @param map   ячейка
     * @param press нажатие
     * @param mine  мина
     */
    public void Input(Cell map, int press, int mine) {
        int result = 0;
        result = recieveClickMAIN(map.getX(), map.getY(), press, mine);
        switch (result) {
            case 0:
                break;
            case -1:
                if (!was_victory) {
                    gameover();
                }
                break;
        }


    }

    /**
     * Вычисляем адрес по кому кликнули
     */
    public int recieveClickMAIN(int x, int y, int press, int mine) {
        if (mine == FIND_MINE) { //если попали на мину
            int result = map[x][y].recieveClickMAIN(x, y, press, mine);
            return result;
        }
        int cell_x = x / CELL_SIZE;
        int cell_y = y / CELL_SIZE;


        int result_open = map[cell_x][cell_y].recieveClickMAIN(x, y, press, mine);


        if (result_open == FIND_MINE) {
            try {
                recieveClickMAIN(x + CELL_SIZE, y, press, mine);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                //ignore
            }
            try {
                recieveClickMAIN(x - CELL_SIZE, y, press, mine);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                //ignore
            }
            try {
                recieveClickMAIN(x, y + CELL_SIZE, press, mine);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                //ignore
            }
            try {
                recieveClickMAIN(x, y - CELL_SIZE, press, mine);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                //ignore
            }
            return 0;
        }

        return result_open;
    }


    public void gameover() {
        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            this.streamIdExplode = this.sp.play(this.soundIdExplode, 1, 1, 1, 0, 1);
        }
        GM = true;
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons.length; j++) {
                buttons[i][j].setClickable(false);
                map[i][j].show(map[i][j]);
                buttons[i][j].setBackgroundResource(map[i][j].getSprite(map[i][j]));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.Back) {
            mainLL.removeAllViews();
            setContentView(this.StartMenu);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}