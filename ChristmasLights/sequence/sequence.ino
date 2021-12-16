/*
  Sequence
  ========
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
void onOn(int light1, int light2)
{
  digitalWrite(light1, HIGH);  
  digitalWrite(light2, HIGH);  
  pause();
}
void offOff(int light1, int light2)
{
  digitalWrite(light1, LOW);  
  digitalWrite(light2, LOW);  
}

void loop() 
{
  // PART 1
  
  // 1 -> 7
  on(LIGHT_1);
  offOn(LIGHT_1,LIGHT_2);
  offOn(LIGHT_2,LIGHT_3);
  offOn(LIGHT_3,LIGHT_4);
  offOn(LIGHT_4,LIGHT_5);
  offOn(LIGHT_5,LIGHT_6);
  offOn(LIGHT_6,LIGHT_7);

  // 12 <- 67
  on(LIGHT_6);
  offOn(LIGHT_7,LIGHT_5);
  offOn(LIGHT_6,LIGHT_4);
  offOn(LIGHT_5,LIGHT_3);
  offOn(LIGHT_4,LIGHT_2);
  offOn(LIGHT_3,LIGHT_1);
  
  // 23 -> 67
  offOn(LIGHT_1,LIGHT_3);
  offOn(LIGHT_2,LIGHT_4);
  offOn(LIGHT_3,LIGHT_5);
  offOn(LIGHT_4,LIGHT_6);
  offOn(LIGHT_5,LIGHT_7);

  // 0 <- 7
  off(LIGHT_6);
  pause();
  offOn(LIGHT_7,LIGHT_6);
  offOn(LIGHT_6,LIGHT_5);
  offOn(LIGHT_5,LIGHT_4);
  offOn(LIGHT_4,LIGHT_3);
  offOn(LIGHT_3,LIGHT_2);
  offOn(LIGHT_2,LIGHT_1);
  off(LIGHT_1);
  pause();


  // PART 2

  // 17, 26, 35, 4,
  onOn(LIGHT_1, LIGHT_7);
  offOff(LIGHT_1, LIGHT_7);
  onOn(LIGHT_2, LIGHT_6);
  offOff(LIGHT_2, LIGHT_6);
  onOn(LIGHT_3, LIGHT_5);
  offOff(LIGHT_3, LIGHT_5);
  on(LIGHT_4);

  // 35, 26, 17, 0
  off(LIGHT_4);
  onOn(LIGHT_3, LIGHT_5);
  offOff(LIGHT_3, LIGHT_5);
  onOn(LIGHT_2, LIGHT_6);
  offOff(LIGHT_2, LIGHT_6);
  onOn(LIGHT_1, LIGHT_7);
  offOff(LIGHT_1, LIGHT_7);
  pause();
}
