package com.ngx.nix;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.p003v4.app.Fragment;
import android.support.p003v4.view.ViewCompat;
import android.support.p006v7.widget.helper.ItemTouchHelper.Callback;
import android.text.Layout.Alignment;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.ngx.mp100sdk.Enums.Alignments;
import com.ngx.mp100sdk.Enums.NGXBarcodeCommands;
import com.ngx.mp100sdk.NGXPrinter;
import com.ngx.nix.selector.FileSelector;
import com.ngx.nix.selector.OnHandleFileListener;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MP100MainFragment extends Fragment {
    private static final int RESULT_LOAD_IMAGE = 99;
    /* access modifiers changed from: private */
    public static String alignment = "LEFT";
    public final String fileName = "image/ngx.png";
    /* access modifiers changed from: private */
    public boolean isBoldStyleSet = false;
    /* access modifiers changed from: private */
    public EditText mEdit;
    final String[] mFileFilter = {".png", ".bmp", ".jpeg", ".jpg"};
    /* access modifiers changed from: private */
    public FileSelector mFileSel;
    OnHandleFileListener mLoadFileListener = new OnHandleFileListener() {
        public void handleFile(String filePath) {
            try {
                MP100MainFragment.this.ngxPrinter.printLogo(Uri.parse(filePath).getPath());
            } catch (Exception e) {
                Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                e.printStackTrace();
            }
            MP100MainFragment.this.mFileSel.dismiss();
        }
    };
    /* access modifiers changed from: private */
    public NGXPrinter ngxPrinter = MP100Main.ngxPrinter;
    public OnClickListener onBtnAlignmentClicked = new OnClickListener() {
        public void onClick(View view) {
            switch (view.getId()) {
                case C0425R.C0427id.btnLeftAlign /*2131558558*/:
                    MP100MainFragment.alignment = "LEFT";
                    return;
                case C0425R.C0427id.btnCenterAlign /*2131558559*/:
                    MP100MainFragment.alignment = "CENTER";
                    return;
                case C0425R.C0427id.btnRightAlign /*2131558560*/:
                    MP100MainFragment.alignment = "RIGHT";
                    return;
                default:
                    return;
            }
        }
    };
    public OnClickListener onBtnFontSizeClicked = new OnClickListener() {
        public void onClick(View view) {
            switch (view.getId()) {
                case C0425R.C0427id.btnFontSize16 /*2131558554*/:
                    MP100MainFragment.this.selectedFontSize = 16;
                    return;
                case C0425R.C0427id.btnFontSize20 /*2131558555*/:
                    MP100MainFragment.this.selectedFontSize = 20;
                    return;
                case C0425R.C0427id.btnFontSize24 /*2131558556*/:
                    MP100MainFragment.this.selectedFontSize = 24;
                    return;
                case C0425R.C0427id.btnFontSize28 /*2131558557*/:
                    MP100MainFragment.this.selectedFontSize = 28;
                    return;
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public int selectedFontSize = 24;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(C0425R.layout.fragment_mp100_main, container, false);
        initControls(view);
        return view;
    }

    private void initControls(View v) {
        this.mEdit = (EditText) v.findViewById(C0425R.C0427id.txt);
        ((Button) v.findViewById(C0425R.C0427id.btnPrintText)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String str = MP100MainFragment.this.mEdit.getText().toString();
                if (str.length() == 0) {
                    Toast.makeText(MP100MainFragment.this.getActivity(), "Nothing to print", 1).show();
                    return;
                }
                try {
                    String access$000 = MP100MainFragment.alignment;
                    char c = 65535;
                    switch (access$000.hashCode()) {
                        case 2332679:
                            if (access$000.equals("LEFT")) {
                                c = 0;
                                break;
                            }
                            break;
                        case 77974012:
                            if (access$000.equals("RIGHT")) {
                                c = 2;
                                break;
                            }
                            break;
                        case 1984282709:
                            if (access$000.equals("CENTER")) {
                                c = 1;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            MP100MainFragment.this.ngxPrinter.printText(str, Alignments.LEFT, MP100MainFragment.this.selectedFontSize);
                            return;
                        case 1:
                            MP100MainFragment.this.ngxPrinter.printText(str, Alignments.CENTER, MP100MainFragment.this.selectedFontSize);
                            return;
                        case 2:
                            MP100MainFragment.this.ngxPrinter.printText(str, Alignments.RIGHT, MP100MainFragment.this.selectedFontSize);
                            return;
                        default:
                            MP100MainFragment.this.ngxPrinter.printText(str, Alignments.LEFT, MP100MainFragment.this.selectedFontSize);
                            return;
                    }
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
        ((Button) v.findViewById(C0425R.C0427id.btnPrintLineFeed)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    MP100MainFragment.this.ngxPrinter.lineFeed(1);
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
        ((Button) v.findViewById(C0425R.C0427id.btnPrintUNICODEText)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MP100MainFragment.this.printKannadaBill();
                MP100MainFragment.this.printHindiBill();
                MP100MainFragment.this.printEnglishBill();
            }
        });
        ((Button) v.findViewById(C0425R.C0427id.btnPrintKannadaText)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MP100MainFragment.this.mEdit.setText(MP100MainFragment.this.getKannadaText());
                MP100MainFragment.this.print();
            }
        });
        ((Button) v.findViewById(C0425R.C0427id.btnPrintHindiText)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MP100MainFragment.this.mEdit.setText(MP100MainFragment.this.getHindiText());
                MP100MainFragment.this.print();
            }
        });
        v.findViewById(C0425R.C0427id.btnPrintEnglistText).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MP100MainFragment.this.mEdit.setText(MP100MainFragment.this.getEnglishText());
                MP100MainFragment.this.print();
            }
        });
        v.findViewById(C0425R.C0427id.btnPrintTeluguText).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MP100MainFragment.this.mEdit.setText(MP100MainFragment.this.getTeluguText());
            }
        });
        v.findViewById(C0425R.C0427id.btnPrintTamilText).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MP100MainFragment.this.mEdit.setText(MP100MainFragment.this.getTamilText());
                MP100MainFragment.this.print();
            }
        });
        v.findViewById(C0425R.C0427id.btnSetFontBold).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MP100MainFragment.this.isBoldStyleSet = true;
                try {
                    MP100MainFragment.this.ngxPrinter.setStyleBold();
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
        v.findViewById(C0425R.C0427id.btnSetFontRegular).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MP100MainFragment.this.isBoldStyleSet = false;
            }
        });
        v.findViewById(C0425R.C0427id.btnFontSize16).setOnClickListener(this.onBtnFontSizeClicked);
        v.findViewById(C0425R.C0427id.btnFontSize20).setOnClickListener(this.onBtnFontSizeClicked);
        v.findViewById(C0425R.C0427id.btnFontSize24).setOnClickListener(this.onBtnFontSizeClicked);
        v.findViewById(C0425R.C0427id.btnFontSize28).setOnClickListener(this.onBtnFontSizeClicked);
        v.findViewById(C0425R.C0427id.btnLeftAlign).setOnClickListener(this.onBtnAlignmentClicked);
        v.findViewById(C0425R.C0427id.btnCenterAlign).setOnClickListener(this.onBtnAlignmentClicked);
        v.findViewById(C0425R.C0427id.btnRightAlign).setOnClickListener(this.onBtnAlignmentClicked);
        v.findViewById(C0425R.C0427id.btnPrintLogo).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MP100MainFragment.this.dialogBox();
            }
        });
        v.findViewById(C0425R.C0427id.btnTestBill).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy '@'HH:mm", Locale.getDefault());
                    Date date = new Date();
                    MP100MainFragment.this.ngxPrinter.setDefault();
                    MP100MainFragment.this.ngxPrinter.setStyleDoubleWidth();
                    MP100MainFragment.this.ngxPrinter.printText("CASH BILL", Alignments.CENTER, 30);
                    MP100MainFragment.this.ngxPrinter.setStyleNormal();
                    MP100MainFragment.this.ngxPrinter.printText("DT: " + format.format(date), Alignments.LEFT, 24);
                    MP100MainFragment.this.ngxPrinter.printText("BillNumber: 00000001", Alignments.LEFT, 24);
                    MP100MainFragment.this.ngxPrinter.lineFeed(1);
                    MP100MainFragment.this.ngxPrinter.setStyleBold();
                    MP100MainFragment.this.ngxPrinter.printText("Item    Qty    Price   Total", Alignments.LEFT, 24);
                    MP100MainFragment.this.ngxPrinter.setStyleNormal();
                    MP100MainFragment.this.ngxPrinter.printText("------------------------------", Alignments.LEFT, 24);
                    MP100MainFragment.this.ngxPrinter.printText("Brown  3PC    35.00    105.00", Alignments.LEFT, 26);
                    MP100MainFragment.this.ngxPrinter.printText("LP 6 Oil SHA\n\t\t\t\t\t\t\t9PC    35.00    315.00", Alignments.LEFT, 26);
                    MP100MainFragment.this.ngxPrinter.printText("WC 3 IN 1\n\t\t\t\t\t\t 5PC    25.00    125.00", Alignments.LEFT, 26);
                    MP100MainFragment.this.ngxPrinter.lineFeed();
                    MP100MainFragment.this.ngxPrinter.printText("------------------------------", Alignments.LEFT, 24);
                    MP100MainFragment.this.ngxPrinter.setStyleDoubleHeight();
                    MP100MainFragment.this.ngxPrinter.printText("TOTAL          Rs. 545.00", Alignments.RIGHT, 30);
                    MP100MainFragment.this.ngxPrinter.setStyleNormal();
                    MP100MainFragment.this.ngxPrinter.printText("------------------------------", Alignments.LEFT, 24);
                    MP100MainFragment.this.ngxPrinter.printText("PAID Rs. 550.00", Alignments.LEFT, 26);
                    MP100MainFragment.this.ngxPrinter.printText("CHANGE Rs. 5.00", Alignments.LEFT, 26);
                    MP100MainFragment.this.ngxPrinter.lineFeed(5);
                    MP100MainFragment.this.ngxPrinter.setDefault();
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
        v.findViewById(C0425R.C0427id.btnSetFontDoubleWidth).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    MP100MainFragment.this.ngxPrinter.setStyleDoubleWidth();
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
        v.findViewById(C0425R.C0427id.btnSetFontDoubleHeight).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    MP100MainFragment.this.ngxPrinter.setStyleDoubleHeight();
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
        v.findViewById(C0425R.C0427id.btnSetFontNormal).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    MP100MainFragment.this.ngxPrinter.setStyleNormal();
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
        v.findViewById(C0425R.C0427id.btnCODABAR).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    MP100MainFragment.this.ngxPrinter.printBarcode("A12588963B", NGXBarcodeCommands.CODABAR, 100, 380);
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
        v.findViewById(C0425R.C0427id.btnQRCode).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    MP100MainFragment.this.ngxPrinter.printBarcode("http://ngxtech.com", NGXBarcodeCommands.QRCode, Callback.DEFAULT_DRAG_ANIMATION_DURATION, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
        v.findViewById(C0425R.C0427id.btnCode39).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    String Str = MP100MainFragment.this.mEdit.getText().toString();
                    if (Str.length() == 0) {
                        MP100MainFragment.this.ngxPrinter.printBarcode("ABC123$ -", NGXBarcodeCommands.CODE39, 100, 380);
                        MP100MainFragment.this.ngxPrinter.lineFeed(4);
                        MP100MainFragment.this.ngxPrinter.printBarcode("1478523697894561230", NGXBarcodeCommands.CODE39, 100, 384);
                        MP100MainFragment.this.ngxPrinter.lineFeed(4);
                        return;
                    }
                    MP100MainFragment.this.ngxPrinter.printBarcode(Str, NGXBarcodeCommands.CODE39, 100, 380);
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
        v.findViewById(C0425R.C0427id.btnEAN8).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    MP100MainFragment.this.ngxPrinter.printBarcode("15896347", NGXBarcodeCommands.JAN8_EAN8, 100, 380);
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
        v.findViewById(C0425R.C0427id.btnEAN13).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    MP100MainFragment.this.ngxPrinter.printBarcode("8902519500557", NGXBarcodeCommands.JAN13_EAN13, 100, 380);
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
        v.findViewById(C0425R.C0427id.btnITF).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    MP100MainFragment.this.ngxPrinter.printBarcode("789123456072", NGXBarcodeCommands.ITF, 100, 380);
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
        v.findViewById(C0425R.C0427id.btnCode128).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    MP100MainFragment.this.ngxPrinter.printBarcode("7891ABCabc()#6072", NGXBarcodeCommands.CODE128, 100, 380);
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
        v.findViewById(C0425R.C0427id.btnPDF417).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    MP100MainFragment.this.ngxPrinter.printBarcode("7891ABCabc()#6072", NGXBarcodeCommands.PDF417, 100, 380);
                } catch (Exception e) {
                    Toast.makeText(MP100MainFragment.this.getContext(), e.getMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 99 && resultCode == -1 && data != null) {
            String[] filePathColumn = {"_data"};
            Cursor cursor = getActivity().getContentResolver().query(data.getData(), filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                String picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                cursor.close();
                Uri fileUri = Uri.parse(picturePath);
                if (fileUri != null) {
                    try {
                        this.ngxPrinter.printLogo(fileUri.getPath());
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), 0);
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "media handler not available, choose another image", 0).show();
                }
            }
        }
    }

    public void dialogBox() {
        CharSequence[] storage = {"Select from Gallery"};
        Builder builder = new Builder(getActivity());
        builder.setTitle("Pick a storage");
        builder.setItems(storage, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        MP100MainFragment.this.startActivityForResult(new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI), 99);
                        return;
                    default:
                        return;
                }
            }
        });
        builder.show();
    }

    /* access modifiers changed from: private */
    public void print() {
        try {
            String str = this.mEdit.getText().toString();
            if (str.length() == 0) {
                Toast.makeText(getActivity(), "Nothing to print", 1).show();
                return;
            }
            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/DroidSansMono.ttf");
            TextPaint tp = new TextPaint();
            tp.setColor(ViewCompat.MEASURED_STATE_MASK);
            tp.setTextSize((float) this.selectedFontSize);
            if (this.isBoldStyleSet) {
                tp.setTypeface(Typeface.create(tf, 1));
            } else {
                tp.setTypeface(tf);
            }
            Log.i("NGX", "printing start");
            String str2 = alignment;
            char c = 65535;
            switch (str2.hashCode()) {
                case 2332679:
                    if (str2.equals("LEFT")) {
                        c = 0;
                        break;
                    }
                    break;
                case 77974012:
                    if (str2.equals("RIGHT")) {
                        c = 2;
                        break;
                    }
                    break;
                case 1984282709:
                    if (str2.equals("CENTER")) {
                        c = 1;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    this.ngxPrinter.printUnicodeText(str, Alignment.ALIGN_NORMAL, tp);
                    break;
                case 1:
                    this.ngxPrinter.printUnicodeText(str, Alignment.ALIGN_CENTER, tp);
                    break;
                case 2:
                    this.ngxPrinter.printUnicodeText(str, Alignment.ALIGN_OPPOSITE, tp);
                    break;
                default:
                    this.ngxPrinter.printUnicodeText(str, Alignment.ALIGN_NORMAL, tp);
                    break;
            }
            this.ngxPrinter.lineFeed(1);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), 0).show();
            e.printStackTrace();
        }
    }

    private String shortBillSample() {
        String separator = "-----------------------------\n";
        String str = "DT: " + new SimpleDateFormat("dd/MM/yyyy '@'HH:mm", Locale.getDefault()).format(new Date()) + "\n" + separator + "Item        Qty    Price    Total\n" + separator;
        for (int i = 0; i < 20; i++) {
            str = str + "Brown         3PC    35.00    105.00\nLP 6 Oil SHA  9PC    30.00    270.00\nWC 3 IN 1     5PC    25.00    125.00\nWC GENTLE S  36PC    35.00    1260.00\n";
        }
        return str + separator + "Total         1760.00";
    }

    private String longBillSample() {
        return "--------------------------------\n\t\tITEM           QTY\t   PRICE\n--------------------------------\nAPASARA PENCILS      250\t100\nBILLING MACHINES  \t  15\t100\nNAIL PAINT PINK     2600   100\nPEPSODENT PASTE      100\t104\nSAMSUNG GRAND         29   100\nBEAUTY TOOLS      \t  35   100\nNAIL POLISHES         70   100\nEYE SHADOWS       \t  10   108\nICE CREAM             16   100\nVIM BAR SOAPS         10\t110\nFACE WASH PONDS       10   111\nFACE PACK CREAM  \t  10   112\nCOLGATE TH PST\t\t 150 \t100\nHAIR CLR PWDR \t  \t 156\t100\nKING SIZE NT BOOK \t   5\t103.5\nSML SIZE NOTE BOO    250\t 10\nVEGETABLES,FRUITS    100\t117\nWATERMELON JUICE      10\t118\nAPPLE JUICE      \t 250    50\nAPASARA PENCILS      100   120\n--------------------------------\nTOTAL AMOUNT : \t   382307.50\n--------------------------------\n            THANK YOU        \n";
    }

    /* access modifiers changed from: private */
    public String getKannadaText() {
        return "ನಾವು ಮತ್ತು ನಮ್ಮ ದೇಶ\nನಾವೆಲ್ಲ ಭಾರತೀಯರು. ನಾವು ನಮ್ಮ ದೇಶದ ಯಾವುದೇ ಪ್ರಾಂತ್ಯದ ನಿವಾಸಿಗಳಾಗಿರಬಹುದು ಅಥವಾ ಯಾವುದೇ ಜಾತಿ ಅಥವಾ ಮತಕ್ಕೆ ಸೇರಿರಬಹುದು, ಅಥವಾ ಯಾವುದೇ ಭಾಷೆಯನ್ನಾಡುವವರಾಗಿರಬಹುದು, ನಾವೆಲ್ಲಾ ಭಾರತೀಯರು.\nನಮ್ಮ ಸಂಸ್ಕೃತಿ ಪುರಾತನವಾದದ್ದು. ನಮ್ಮ ದೇಶದಲ್ಲಿ ಅನೇಕ ಮಹಾನ್ ವ್ಯಕ್ತಿಗಳು, ಸಾದು ಸಂತರು, ತತ್ವ ಜ್ಞಾನಿಗಳು, ದೇಶ ಭಕ್ತರು, ಕವಿಗಳು, ಸಾಹಿತಿಗಳು, ಗಣಿತ, ವಿಜ್ಞಾನ, ಜ್ಯೋತಿಷ್ಯ, ಅಯುರ್ವೇದ ಮುಂತಾದ ಅನೇಕ ವಿಚಾರಗಳಲ್ಲಿ ಪಂಡಿತರು, ವೀರರು, ಶಿಲ್ಪ, ಸಂಗೀತ, ನಾಟ್ಯ, ನಾಟಕ, ಕುಶಲ ಕಲೆಗಳಲ್ಲಿ ಪರಿಣಿತರು ಹುಟ್ಟಿ ನಮ್ಮ ದೇಶದ ಹಾಗೂ ಮಾನವ ಕಲ್ಯಾಣಕ್ಕಾಗಿ ದುಡಿದಿದ್ದಾರೆ, ಶ್ರಮಿಸಿದ್ದಾರೆ. ಅವರು ದೇಶಕ್ಕೆ ವಿವಿಧ ಕೊಡುಗೆಗಳನ್ನು ಕೊಟ್ಟು ಮಹದುಪಕಾರ ಮಾಡಿದ್ದಾರೆ.\nಅನೇಕ ದೇಶಭಕ್ತರ ಸತತ ಹೋರಾಟದ ಫಲವಾಗಿ ನಮ್ಮ ದೇಶವು ೧೯೪೭ನೇ ಇಸವಿ ಅಗಸ್‌್ಟ ೧೫ರಂದು ಸ್ವಾತಂತ್ರ್ಯ ಗಳಿಸಿತು. ಅನೇಕರು ಈ ಹೋರಾಟದಲ್ಲಿ ತಮ್ಮ ಪ್ರಾಣವನ್ನೇ ಅರ್ಪಿಸಿದರು. ಮಹಾತ್ಮ ಗಾಂಧೀಜಿಯವರ ಪಾತ್ರವನ್ನು ಇಲ್ಲಿ ಸ್ಮರಿಸಬಹುದು. ಅವರು ಈ ಹೋರಾಟದಲ್ಲಿ ಹಿರಿಯ ನಾಯಕರಾಗಿದ್ದರು. ನಾವು ಸಾಧಿಸಬೇಕಾದ ಗುರಿ ಎಷ್ಟು ಒಳ್ಳೆಯದೋ, ಅದನ್ನು ಸಾಧಿಸುವ ಮಾರ್ಗವೂ ಕೂಡ ಅಷ್ಟೇ ಒಳ್ಳೆಯದಾಗಿರಬೇಕೆಂಬುದೇ ಅವರ ದೃಢ ಮತವಾಗಿತ್ತು. ಅದರಂತೆಯೇ ಅವರು ಸ್ವಾತಂತ್ರ‍್ಯಕ್ಕಾಗಿ ನಡಸಿದ ಹೋರಾಟದಲ್ಲಿ ಅಹಿಂಸಾ ತತ್ವವನ್ನು ಅನುಸರಿಸಿ ಇತರರಿಗೂ ಅದನ್ನೇ ಬೋಧಿಸಿದರು ಮತ್ತು ಮಾರ್ಗದರ್ಶನ ನೀಡಿದರು.\nಇದುವರೆಗೂ ನಮ್ಮ ದೇಶವು ಅನೇಕ ವಿಚಾರಗಳಲ್ಲಿ ಪ್ರಗತಿ ಹೊಂದಿದೆ. ಇನ್ನೂ ಅಗಬೇಕಾದದ್ದು ಅಪಾರವಾಗಿದೆ. ಪ್ರತಿಯೊಬ್ಬರೂ (ಮಕ್ಕಳು, ಯುವಕರು, ಹಿರಿಯರು) ಅವರವರ ಸ್ಥಾನಮಾನಕ್ಕೆ ತಕ್ಕಂತೆ, ದೇಶಾಭಿಮಾನದಿಂದಲೂ, ಒಗ್ಗಟ್ಟನಿಂದಲೂ, ಪ್ರೀತಿ ಸೌಹಾರ್ದಗಳಿಂದಲೂ, ಪ್ರಾಮಾಣಿಕತೆಯಿಂದಲೂ, ಕಾರ್ಯ ನಿಪುಣತೆಯಿಂದಲೂ, ವಿವೇಚನೆಯಿಂದಲೂ, ನಮ್ಮ ರಾಷ್ಟೃದ ರಕ್ಷಣೆ ಮತ್ತು ಪ್ರಗತಿಗಾಗಿ ದುಡಿಯಬೇಕು. ಅಲ್ಲದೆ, ಈ ಕಾರ್ಯಗಳಲ್ಲಿ ನಿರತರಾದವರಿಗೆ ನೆರವು ನೀಡಬೇಕು. ಇದು ದೇಶಕ್ಕೆ ಒಳಿತು, ನಮಗೂ ಒಳಿತು.";
    }

    /* access modifiers changed from: private */
    public String getHindiText() {
        return "कम्प्यूटर, मूल रूप से, नंबरों से सम्बंध रखते हैं। ये प्रत्येक अक्षर और वर्ण के लिए एक नंबर निर्धारित करके अक्षर और वर्ण संग्रहित करते हैं। यूनिकोड का आविष्कार होने से पहले, ऐसे नंबर देने के लिए सैंकडों विभिन्न संकेत लिपि प्रणालियां थीं। किसी एक संकेत लिपि में पर्याप्त अक्षर नहीं हो सकते हैं : उदाहरण के लिए, यूरोपिय संघ को अकेले ही, अपनी सभी भाषाऒं को कवर करने के लिए अनेक विभिन्न संकेत लिपियों की आवश्यकता होती है। अंग्रेजी जैसी भाषा के लिए भी, सभी अक्षरों, विरामचिन्हों और सामान्य प्रयोग के तकनीकी प्रतीकों हेतु एक ही संकेत लिपि पर्याप्त नहीं थी।\n";
    }

    /* access modifiers changed from: private */
    public String getEnglishText() {
        return "India, officially the Republic of India is a country in South Asia. It is the seventh-largest country by area, the second-most populous country (with over 1.2 \nbillion people), and the most populous democracy in the world. Bounded by the Indian Ocean on the south, the Arabian Sea on the south-west, and the Bay of Bengal \non the south-east, it shares land borders with Pakistan to the west;[d] China, Nepal, and Bhutan to the north-east; and Myanmar (Burma) and Bangladesh to the \neast. In the Indian Ocean, India is in the vicinity of Sri Lanka and the Maldives; in addition, India's Andaman and Nicobar Islands share a maritime border withThailand and Indonesia.";
    }

    /* access modifiers changed from: private */
    public String getTeluguText() {
        return "జరిగిందంతా చూస్తూ \nఎరగనట్లు పడి ఉండగ \nసాక్షీ భూతుణ్ణి గాను \nసాక్షాత్తూ మానవుణ్ణి";
    }

    /* access modifiers changed from: private */
    public String getTamilText() {
        return "யூனிக்கோடு எந்த இயங்குதளம் ஆயினும், எந்த நிரல் ஆயினும், எந்த மொழி ஆயினும் ஒவ்வொரு எழுத்துக்கும் தனித்துவமான எண் ஒன்றை வழங்குகிறது.";
    }

    private String getAllLanguageText() {
        return "ಕನ್ನಡ ಮುದ್ರಣ\n" + getKannadaText() + "\n\nहिंदी मुद्रण\n" + getHindiText() + "\n\nEnglish Print\n" + getEnglishText() + "\n\nతెలుగు ప్రింటింగ్\n" + getTeluguText() + "\n\nதமிழ் அச்சிடுதல்\n" + getTamilText() + "\n";
    }

    /* access modifiers changed from: private */
    public void printKannadaBill() {
        String separator = "--------------------------------";
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/DroidSansMono.ttf");
        Bitmap bmp = getBitmapFromAssets("image/ngx.png");
        this.ngxPrinter.addImage(bmp);
        bmp.recycle();
        TextPaint tp = new TextPaint();
        tp.setTypeface(Typeface.create(tf, 1));
        tp.setTextSize(30.0f);
        tp.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.ngxPrinter.addText("ನಗದು ಬಿಲ್ಲು", Alignment.ALIGN_CENTER, tp);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ದಿನಾಂಕ: 31-05-2017  ಬಿಲ್ ಸಂಖ್ಯೆ: 001\n");
        stringBuilder.append(separator);
        stringBuilder.append("\n");
        stringBuilder.append("ಹೆಸರು: ವಿನಾಯಕ\n");
        stringBuilder.append("ಸ್ಥಳ: ಬೆಂಗಳೂರು\n");
        stringBuilder.append(separator);
        stringBuilder.append("\n");
        stringBuilder.append("ವಿವರಣೆ        ಪ್ರಮಾಣ   ದರ    ಮೊತ್ತ\n");
        stringBuilder.append(separator);
        stringBuilder.append("\n");
        stringBuilder.append("ರೆನಾಲ್ಡ್ಸ್ ಪೆನ್        2   10     20\n");
        stringBuilder.append("ನಟರಾಜ್ ಎರೇಸರ್    10    5     50\n");
        stringBuilder.append(separator);
        stringBuilder.append("\n");
        stringBuilder.append("ಒಟ್ಟು ಐಟಂಗಳು: 2       ಮೊತ್ತ:  66.50\n");
        stringBuilder.append("ಒಟ್ಟು ಪ್ರಮಾಣ: 12   ವ್ಯಾಟ್ ಮೊತ್ತ: 3.50\n");
        stringBuilder.append("                  -------------");
        tp.setTextSize(20.0f);
        this.ngxPrinter.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);
        stringBuilder.setLength(0);
        stringBuilder.append("      ನಿವ್ವಳ ಮೊತ್ತ: 70.00\n");
        tp.setTextSize(25.0f);
        this.ngxPrinter.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);
        stringBuilder.setLength(0);
        stringBuilder.append("ಪಾವತಿ ಮೋಡ್: ನಗದು\n\n\n");
        tp.setTextSize(20.0f);
        this.ngxPrinter.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);
        try {
            this.ngxPrinter.print();
            this.ngxPrinter.lineFeed(2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void printHindiBill() {
        String separator = "--------------------------------";
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/DroidSansMono.ttf");
        Bitmap bmp = getBitmapFromAssets("image/ngx.png");
        this.ngxPrinter.addImage(bmp);
        bmp.recycle();
        TextPaint tp = new TextPaint();
        tp.setTypeface(Typeface.create(tf, 1));
        tp.setTextSize(30.0f);
        tp.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.ngxPrinter.addText("नकद बिल", Alignment.ALIGN_CENTER, tp);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("दिनांक: 31-05-2017    बिल संख्या: 001\n");
        stringBuilder.append(separator);
        stringBuilder.append("\n");
        stringBuilder.append("नाम: विनायक\n");
        stringBuilder.append("स्थान: बैंगलोर\n");
        stringBuilder.append(separator);
        stringBuilder.append("\n");
        stringBuilder.append("विवरण         मात्रा     मूल्य     रकम\n");
        stringBuilder.append(separator);
        stringBuilder.append("\n");
        stringBuilder.append("रेनॉल्ड्स पेन      2      10      20\n");
        stringBuilder.append("नटराज इरेज़र     10      5      50\n");
        stringBuilder.append(separator);
        stringBuilder.append("\n");
        stringBuilder.append("कुल सामान: 2            रकम: 66.50\n");
        stringBuilder.append("कुल मात्रा :12         वैट रकम:  3.50\n");
        stringBuilder.append("                  --------------");
        tp.setTextSize(20.0f);
        this.ngxPrinter.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);
        stringBuilder.setLength(0);
        stringBuilder.append("            नेट रकम: 70.00");
        tp.setTextSize(25.0f);
        this.ngxPrinter.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);
        stringBuilder.setLength(0);
        stringBuilder.append("भुगतान मोड: नकद\n\n\n");
        tp.setTextSize(20.0f);
        this.ngxPrinter.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);
        try {
            this.ngxPrinter.print();
            this.ngxPrinter.lineFeed(2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void printEnglishBill() {
        String separator = "--------------------------------";
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/DroidSansMono.ttf");
        Bitmap bmp = getBitmapFromAssets("image/ngx.png");
        this.ngxPrinter.addImage(bmp);
        bmp.recycle();
        TextPaint tp = new TextPaint();
        tp.setTypeface(Typeface.create(tf, 1));
        tp.setTextSize(30.0f);
        tp.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.ngxPrinter.addText("Cash Bill", Alignment.ALIGN_CENTER, tp);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Date: 31-05-2017  Bill No: 001\n");
        stringBuilder.append(separator);
        stringBuilder.append("\n");
        stringBuilder.append("Name: Vinayak\n");
        stringBuilder.append("Place: Bangalore\n");
        stringBuilder.append(separator);
        stringBuilder.append("\n");
        stringBuilder.append("Particulars    Qty   Rate    Amt\n");
        stringBuilder.append(separator);
        stringBuilder.append("\n");
        stringBuilder.append("Reynolds Pen     2     10     20\n");
        stringBuilder.append("Nataraj Eraser  10      5     50\n");
        stringBuilder.append(separator);
        stringBuilder.append("\n");
        stringBuilder.append("Tot Items: 2      Amount: 66.50\n");
        stringBuilder.append("Tot Qty  :12     Vat Amt:  3.50\n");
        stringBuilder.append("                  -------------");
        tp.setTextSize(20.0f);
        this.ngxPrinter.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);
        stringBuilder.setLength(0);
        stringBuilder.append("           Net Amt: 70.00");
        tp.setTextSize(25.0f);
        this.ngxPrinter.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);
        stringBuilder.setLength(0);
        stringBuilder.append("Payment Mode: CASH\n\n\n");
        tp.setTextSize(20.0f);
        this.ngxPrinter.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);
        try {
            this.ngxPrinter.print();
            this.ngxPrinter.lineFeed(2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Bitmap getBitmapFromAssets(String fileName2) {
        InputStream istr = null;
        try {
            istr = getActivity().getAssets().open(fileName2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(istr);
    }
}
