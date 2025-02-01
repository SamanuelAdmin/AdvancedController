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



void powerOff() {
  Serial.println("Power off");

  clearScreen();
  drawPowerButton(tft.width() / 2, tft.height() / 2, 60, BLUE);

  delay(500);

  TSPoint p = ts.getPoint();

  if (p.z < 1000 && p.z != 0 && p.z > -1000) {
    sendCommand("poweroff");
  }
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

  if (p.z < 1000 && p.z != 0 && p.z > -1000) {
    Serial.println("Detected: " + (String) p.x + " " + (String) p.y);

    if (p.y > -900) powerOff();
    else if (p.y < -900) {
      Serial.println("Sleep");
      
      delay(500);
    }
  }

  if (millis() % 1000 == 0) {
    Serial.println("Remove command from buffer");
    tft.fillScreen(BLACK);
  }
}
