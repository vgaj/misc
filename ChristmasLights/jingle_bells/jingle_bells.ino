/*
  Jingle Bells
  ============
*/

int LIGHT_1 = 1;
int LIGHT_2 = 2;
int LIGHT_3 = 3;
int LIGHT_4 = 4;
int LIGHT_5 = 5;
int LIGHT_6 = 6;
int LIGHT_7 = 7;

void setup() {
  pinMode(LIGHT_1, OUTPUT);
  pinMode(LIGHT_2, OUTPUT);
  pinMode(LIGHT_3, OUTPUT);
  pinMode(LIGHT_4, OUTPUT);
  pinMode(LIGHT_5, OUTPUT);
  pinMode(LIGHT_6, OUTPUT);
  pinMode(LIGHT_7, OUTPUT);
}

void singleOn(int light, int duration)
{
  digitalWrite(light, HIGH);
  delay(duration * 100);
  digitalWrite(light, LOW);
}

void pause()
{
  delay(500);  
}

void allOn()
{
  digitalWrite(LIGHT_1, HIGH);
  delay(100);
  digitalWrite(LIGHT_2, HIGH);
  delay(100);
  digitalWrite(LIGHT_3, HIGH);
  delay(100);
  digitalWrite(LIGHT_4, HIGH);
  delay(100);
  digitalWrite(LIGHT_5, HIGH);
  delay(100);
  digitalWrite(LIGHT_6, HIGH);
  delay(100);
  digitalWrite(LIGHT_7, HIGH);
  delay(500);
  digitalWrite(LIGHT_1, LOW);
  digitalWrite(LIGHT_2, LOW);
  digitalWrite(LIGHT_3, LOW);
  digitalWrite(LIGHT_4, LOW);
  digitalWrite(LIGHT_5, LOW);
  digitalWrite(LIGHT_6, LOW);
  digitalWrite(LIGHT_7, LOW);
  delay(500);
}

void loop() {

  // Jingle bells (1)
  singleOn(LIGHT_1, 5);
  singleOn(LIGHT_2, 5);
  singleOn(LIGHT_3, 8);

  // Jingle bells (2)
  singleOn(LIGHT_1, 5);
  singleOn(LIGHT_2, 5);
  singleOn(LIGHT_3, 8);
  
  // Jingle all the way
  singleOn(LIGHT_1, 5);
  singleOn(LIGHT_2, 5);
  singleOn(LIGHT_3, 8);
  singleOn(LIGHT_4, 2);
  singleOn(LIGHT_5, 10);
  
  // Oh, what fun it is to ride
  singleOn(LIGHT_1, 5);
  singleOn(LIGHT_2, 5);
  singleOn(LIGHT_3, 8);
  singleOn(LIGHT_4, 2);
  singleOn(LIGHT_5, 5);
  singleOn(LIGHT_6, 5);
  singleOn(LIGHT_7, 5);
    
  // In a one horse open sleigh
  singleOn(LIGHT_1, 3);
  singleOn(LIGHT_2, 2);
  singleOn(LIGHT_3, 5);
  singleOn(LIGHT_4, 5);
  singleOn(LIGHT_5, 5);
  singleOn(LIGHT_6, 5);
  singleOn(LIGHT_7, 10);

  allOn();
   
}
