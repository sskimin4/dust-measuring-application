#include <SoftwareSerial.h> //시리얼통신 라이브러리 호출
#include <SDS011.h>
float p10, p25;
int error;

SDS011 my_sds;
float Value[2]={0,0};
String value1, value2;
String integer1 = "";
String decimal1 = "";
String integer2 = "";
String decimal2 = "";
int i, flag, residue;
int divisor = 64;
String checkSumString = "";
byte buffer[1024];
byte bufferPosition;
int blueTx=(uint8_t)D7;   //Tx (보내는핀 설정)at
int blueRx=(uint8_t)D8;   //Rx (받'는핀 설정)
SoftwareSerial mySerial(blueTx, blueRx);  //시리얼 통신을 위한 객체선언
 
void setup() 
{
  Serial.begin(9600);   //시리얼모니터
  my_sds.begin(D1, D2); //RX, TX
  mySerial.begin(9600); //블루투스 시리얼
  bufferPosition=0;
}

void loop()
{
  error = my_sds.read(&p25, &p10);
  Value[0]=p25;
  Value[1]=p10;
  checkSum();
  sendAndroidValues();
  
  delay(2000);
}

void checkSum()
{
 value1 = String(Value[1]);
 value2 = String(Value[2]);
 flag = 0;
 for(i = 0; i < value1.length(); i++){
  if(value1[i] == '.'){
   flag = 1;
   continue;
  }
  if(flag == 0)
   integer1 += value1[i];
  else
   decimal1 += value1[i];
 }
 flag = 0;
 for(i = 0; i < value2.length(); i++){
  if(value2[i] == '.'){
   flag = 1;
   continue;
  }
  if(flag == 0)
   integer2 += value2[i];
  else
   decimal2 += value2[i];
 }
 residue = (integer1 + decimal1 + integer2 + decimal2) % divisor;
 checkSumString = "#" + value1 + "+" + value2 + "+" + residue + "~";
}


void sendAndroidValues()
{
 mySerial.print(checkSumString);
 mySerial.println();
 delay(1000); 
 
 Serial.print('#');
 //for loop cycles through 2 sensors and sends values via serial
 for(int k=0; k<2; k++)
 {
   Serial.print(Value[k]);
   Serial.print('+');
   //technically not needed but I prefer to break up data values
   //so they are easier to see when debugging
 }
 Serial.print(residue);
 Serial.print('~'); //used as an end of transmission character - used in app for string length
 Serial.println();
 delay(10);        //added a delay to eliminate missed transmissions
}
