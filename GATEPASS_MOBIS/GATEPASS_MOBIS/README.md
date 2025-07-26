#IMIN_PrintLib_Android
1、初始化打印机
   mIminPrintUtils = IminPrintUtils.getInstance(TestPrintActivity.this);

2、获取打印机状态
   USB 类型的打印机：
   mIminPrintUtils.getPrinterStatus(IminPrintUtils.PrintConnectType.USB)
   SPI 类型的打印机：
   mIminPrintUtils.getPrinterStatus(IminPrintUtils.PrintConnectType.SPI, new Callback() {
                                                           @Override
                                                           public void callback(int status) {
                                                              if (status == -1 && PrintUtils.getPrintStatus() == -1){
                                                                  Toast.makeText(TestPrintActivity.this, " " + status, Toast.LENGTH_SHORT).show();
                                                              }else {
                                                                  Toast.makeText(TestPrintActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                                                              }
   
                                                           }
   
                                                       });
   
3.打印空行
  mIminPrintUtils.printAndLineFeed();
  mIminPrintUtils.printAndFeedPaper(50);//走纸的距离
4.切纸
  mIminPrintUtils.partialCut(); //切纸的宽度
5.打印文本
  文本最后是“\n”结尾的表示传输 即打印 
  如果不是“\n”结尾的话会存储在缓存区域，待打印别的类型包含清缓存的时候才会打印
  mIminPrintUtils.printText("iMin committed to use advanced technologies to help our business partners digitize their business.We are dedicated in becoming a leading provider of smart business equipment " +
                                                  "in ASEAN countries,assisting our partners to connect, create and utilize data effectively.\n");
6.打印表格
  strings3：表格内容
  colsWidthArr3    对应内容的位置
  colsAlign3      对应内容是否居中 0左边 , 1中间 , 2右边
  colsSize3    对应的字体大小
  mIminPrintUtils.printColumnsText(strings3, colsWidthArr3,
                                                colsAlign3, colsSize3);
7.图片打印
  先对图片进行灰度处理
  mIminPrintUtils.printSingleBitmap(bitmap_black);   
8.设置打印图片的对齐方式                                              
  mIminPrintUtils.printMultiBitmap(mBitmapList1, 1);   //0 1 2                                              
9.设置一维码的高度
  范围 1 ≤ height ≤ 255
  mIminPrintUtils.setBarCodeHeight(80);
10.设置一维码黑色块的宽度
   范围 2 ≤ width ≤ 6 Default  width = 3
   mIminPrintUtils.setBarCodeWidth(2);
11.打印一维码
   一维码类型
   /**
     * Print bar code
     * usd 打印二维码
        * @param printer
        * @param barCodeType    0 ≤ barCodeType ≤ 6 {@link #UPC_A,#UPC_E,#JAN13_OR_EAN13,#JAN8_OR_EAN8,#CODE39,#ITF,#CODABAR}
        *                       UPC-A  k = 11, 12  48 ≤ d ≤ 57  范围 0-9    长度11/12    spi打印不支持/usb打印也不支持     
        *                       UPC-E  k = 11, 12  48 ≤ d ≤ 57 [where d1 = 48]  范围 0-9    长度11/12    spi打印不支持/usb打印也不支持 
        *                       JAN13 / EAN13  k = 12, 13  48 ≤ d ≤ 57                      打印长度===  
        *                       JAN8 / EAN8  k = 7, 8  48 ≤ d ≤ 57                           打印长度===  
        *                       CODE39  1 ≤ k 48 ≤ d ≤ 57, 65 ≤ d ≤ 90,                       打印长度===  
        *                       ITF  2 ≤ k (even number)  48 ≤ d ≤ 57                           打印长度===  
        *                       CODABAR (NW-7) 2 ≤ k 48 ≤ d ≤ 57, 65 ≤ d ≤ 68, 97 ≤ d ≤ 100, d = 36, 43, 45, 46, 47, 58 [where 65 ≤ d1 ≤ 68, 65 ≤ dk ≤ 68,97 ≤ d1 ≤ 100, 97 ≤ dk ≤ 100]
        *                       CODE_128 {A  97 ≤ d ≤ 122      {B  无限制     {C 48 ≤ d ≤ 57
        * @param barCodeContent
        */
        
   mIminPrintUtils.printBarCode(4, "00999978", 1);   
12.打印二维码
   mIminPrintUtils.printQrCode("123456", 0); //0  1  2
13.设置二维码的大小
   mIminPrintUtils.setQrCodeSize(qrCodeSize); 
14.设置二维码的 容错级别
   mIminPrintUtils.setQrCodeErrorCorrectionLev(qrCodeErrorLev);//48  49  51
15.设置二维码左边的边距
   mIminPrintUtils.setLeftMargin(barAndQrLeftSize);
16.直接打印指令   
   IminPrintUtils.getInstance(this).sendRAWData(bytes); //bytes 是十进制的值
    

 
