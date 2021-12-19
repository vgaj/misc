/*
  Sequence - with a single light at a time
  ========================================
*/

int LIGHT_1 = 1;
int LIGHT_2 = 2;
int LIGHT_3 = 3;
int LIGHT_4 = 4;
int LIGHT_5 = 5;
int LIGHT_6 = 6;
int LIGHT_7 = 7;

void setup() 
{
  pinMode(LIGHT_1, OUTPUT);
  pinMode(LIGHT_2, OUTPUT);
  pinMode(LIGHT_3, OUTPUT);
  pinMode(LIGHT_4, OUTPUT);
  pinMode(LIGHT_5, OUTPUT);
  pinMode(LIGHT_6, OUTPUT);
  pinMode(LIGHT_7, OUTPUT);
}

void pause()
{
  delay(500);
}
void off(int light)
{
  digitalWrite(light, LOW);  
}
void on(int light)
{
  digitalWrite(light, HIGH);  
  pause();
}
void offOn(int lightOff, int lightOn)
{
  off(lightOff);
  on(lightOn);
}

void inAndOutAlt()
{
  on(LIGHT_1);
  offOn(LIGHT_1,LIGHT_7);
  offOn(LIGHT_7,LIGHT_2);
  offOn(LIGHT_2,LIGHT_6);
  offOn(LIGHT_6,LIGHT_3);
  offOn(LIGHT_3,LIGHT_5);
  offOn(LIGHT_5,LIGHT_4);
  offOn(LIGHT_4,LIGHT_5);
  offOn(LIGHT_5,LIGHT_3);
  offOn(LIGHT_3,LIGHT_6);
  offOn(LIGHT_6,LIGHT_2);
  offOn(LIGHT_2,LIGHT_7);
  offOn(LIGHT_7,LIGHT_1);
  off(LIGHT_1);
  pause();
}

void leftToRight()
{
    // 1 ... 7
  on(LIGHT_1);
  offOn(LIGHT_1,LIGHT_2);
  offOn(LIGHT_2,LIGHT_3);
  offOn(LIGHT_3,LIGHT_4);
  offOn(LIGHT_4,LIGHT_5);
  offOn(LIGHT_5,LIGHT_6);
  offOn(LIGHT_6,LIGHT_7);
  off(LIGHT_7);
  pause();
}

void rightToLeft()
{
  // 7 ... 1
  on(LIGHT_7);
  offOn(LIGHT_7,LIGHT_6);
  offOn(LIGHT_6,LIGHT_5);
  offOn(LIGHT_5,LIGHT_4);
  offOn(LIGHT_4,LIGHT_3);
  offOn(LIGHT_3,LIGHT_2);
  offOn(LIGHT_2,LIGHT_1);
  off(LIGHT_1);
  pause();
}

void loop() 
{
  leftToRight();
  rightToLeft();
  inAndOutAlt();
  
  rightToLeft();
  leftToRight();
  inAndOutAlt();
}
