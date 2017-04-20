package com.nadina.sapper;

/**
 * MIT License

 Copyright (c) 2017 Nadia

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.

 * Класс для ячейки
 * @author Nadina
 */
public class Cell extends MainActivity {

    /** Коордната поля x */
    private int x;
    /** Коордната поля y */
    private int y;
    /** Состояние ячейки */
    private int state;
    /** Спрятана ли */
    private boolean isHidden = true;
    /** Отмечена ли */
    private boolean isMarked = false;
    /** Победа ли */
    private boolean isVictory = false;


    public int getY() {
        return this.y;
    }


    public int getX() {
        return this.x;
    }

    public void setY(int y) {
        this.y = y;
    }


    public void setX(int x) {
        this.x = x;
    }

    public Cell(int x, int y, int state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public boolean getMarked() {
        return isMarked;
    }

    public boolean getHidden() {
        return isHidden;
    }

    public boolean getVictory() {
        return isVictory;
    }



    public void setMarked(boolean m) {
        this.isMarked = m;
    }

    public void setVictory(boolean v) {
        this.isVictory = v;
    }

    public void incNearMines() {
        if (state >= 0) {
            state++;
        }
    }

    /**
     * Массив с изображениями
     */
    public final static int CONSTS[] =
            {
                    R.drawable.state_for_broken_flag, //0
                    R.drawable.state_for_flag,//1
                    R.drawable.hidden,//2
                    R.drawable.state_for_one, //3
                    R.drawable.state_for_two, //4
                    R.drawable.state_for_three, //5
                    R.drawable.state_for_four, //6
                    R.drawable.mine_pic, //7
                    R.drawable.explosion, //8
                    R.drawable.empty_field, //9
                    R.drawable.state_for_five //10
            };

    /**
     * Получить спрайт
     * @param map ячейка
     * @return картинка
     */
    public int getSprite(Cell map) {
        if (this.getMarked()) {
            if (!this.isHidden && state != -1) {
                ///Если эта клетка не скрыта, и на ней
                ///ошибочно стоит флажок
                return CONSTS[0];
            }
            ///В другом случае
            return CONSTS[1];
        } else if (isHidden) {
            //Не помечена, но скрыта
            return CONSTS[2];
        } else {
            switch (state) {
                case 1: {
                    return CONSTS[3];
                }
                case 2: {
                    return CONSTS[4];
                }
                case 3: {
                    return CONSTS[5];
                }
                case 4: {
                    return CONSTS[6];
                }
                case 5: {
                    return CONSTS[10];
                }
                case -1: {
                    return CONSTS[7];
                }
                case -2: {
                    return CONSTS[8];
                }
                case 0: {
                    return CONSTS[9];
                }
                default: {
                    return 25;

                }
            }

        }
    }

    /**
     * Получен клик
     * @param x координата х
     * @param y координата у
     * @param press нажатие
     * @param mine Мина
     * @return
     */
    @Override
    public int recieveClickMAIN(int x, int y, int press, int mine) {
        if (isHidden) {
            if (press == 0 && this.isMarked == false) {
                this.isHidden = false;

                if (this.state == -1) {
                    ///Если это была мина, меняем состояние
                    ///на взорванную и передаём сигнал назад
                    this.state = -2;
                    return -1;
                }

                if (this.state == 0) {
                    return 1;
                }

            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "" + state;
    }


    public void show(Cell map) {
        this.isHidden = false;
    }

}
