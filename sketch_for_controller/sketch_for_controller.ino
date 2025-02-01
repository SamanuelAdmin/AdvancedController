#include <Adafruit_GFX.h>    // Core graphics library
#include <TftSpfd5408.h> // Hardware-specific library
#include <stdint.h>
#include "TouchScreen.h"


#define LCD_CS A3 // Chip Select goes to Analog 3
#define LCD_CD A2 // Command/Data goes to Analog 2
#define LCD_WR A1 // LCD Write goes to Analog 1
#define LCD_RD A0 // LCD Read goes to Analog 0

#define LCD_RESET A4 // Can alternately just connect to Arduino's reset pin

#define	BLACK   0x0000
#define	BLUE    0x001F
#define	RED     0xF800
#define	GREEN   0x07E0
#define CYAN    0x07FF
#define MAGENTA 0xF81F
#define YELLOW  0xFFE0
#define WHITE   0xFFFF

// for touch screen
#define YP A1  // must be an analog pin, use "An" notation!
#define XM A2  // must be an analog pin, use "An" notation!
#define YM 7   // can be a digital pin
#define XP 6   // can be a digital pin


TftSpfd5408 tft(LCD_CS, LCD_CD, LCD_WR, LCD_RD, LCD_RESET);
TouchScreen ts = TouchScreen(XP, YP, XM, YM, 300);



void clearScreen() {
  tft.fillScreen(BLACK);
}


void drawMoon(int x, int y, int r, uint16_t color) {
    tft.fillCircle(x, y, r, color);
    tft.fillCircle(x - r / 3, y - r / 3, r * 0.8, BLACK);
}

void drawPowerButton(int x, int y, int r, uint16_t color) {
    tft.drawCircle(x, y, r, color);
    tft.drawCircle(x, y, r - 1, color);
    tft.fillRect(x - 3, y - r, 6, r / 5 * 3, color);
}


void drawButtons() {
  clearScreen();

  drawMoon(tft.width() / 4, tft.height() / 2, 40, BLUE);
  drawPowerButton(tft.width() / 4 * 3, tft.height() / 2, 40, BLUE);
}

void sendCommand(String command) {
  Serial.println("Sending command: " + command);
  delay(1000);
}


TSPoint getPoint() {
  TSPoint p = ts.getPoint();
  pinMode(XM, OUTPUT);
  pinMode(YP, OUTPUT);

  return p;
}


void powerOff() {
  Serial.println("Power off");

  clearScreen();
  drawPowerButton(tft.width() / 2, tft.height() / 2, 60, BLUE);

  delay(300);

  for (int i = 0; i < 10; i++) {
    TSPoint p = getPoint();

    if (p.z < 1000 && p.z != 0 && p.z > -1000) {
      sendCommand("do poweroff");

      tft.setCursor(2, 2);
      tft.setTextColor(WHITE);  tft.setTextSize(4);
      tft.println("POWER OFF");

      delay(5000);

      break;
    }

    delay(25);
  }

  drawButtons();
}


void sleepMode() {
  Serial.println("Sleep");

  clearScreen();
  drawMoon(tft.width() / 2, tft.height() / 2, 60, BLUE);

  delay(300);

  for (int i = 0; i < 10; i++) {
    TSPoint p = getPoint();

    if (p.z < 1000 && p.z != 0 && p.z > -1000) {
      sendCommand("do sleep");

      tft.setCursor(2, 2);
      tft.setTextColor(WHITE);  tft.setTextSize(4);
      tft.println("SLEEP");

      delay(5000);

      break;
    }

    delay(25);
  }

  drawButtons();
}


void setup() {
  Serial.begin(9600);
  
  tft.reset();
  tft.begin(0x9341);
  tft.setRotation(1);

  drawButtons();
}

void loop() {
  TSPoint p = ts.getPoint();
  pinMode(XM, OUTPUT);
  pinMode(YP, OUTPUT);

  if (p.z < 1000 && p.z != 0 && p.z > -1000) {
    Serial.println("Detected: " + (String) p.x + " " + (String) p.y);

    if (p.y > -900) powerOff();
    else if (p.y < -900) sleepMode();
  }
}
