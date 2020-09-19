package ru.tpu.russiantpu.utility;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;


//класс для получения строк из формы
public class FormService {

    //метод получения группы, если
    public String getGroup(TextView groupInput, List<String> groupNames) {
        return groupInput.getText().toString().equals(groupNames.get(0)) ? null : groupInput.getText().toString();
    }

    //метод получения выбранного пола из спиннера
    public String getSelectedGender(Spinner genderInput) {
        String gender = null;
        switch (genderInput.getSelectedItemPosition()) {
            case 1:
                gender = "Male";
                break;
            case 2:
                gender = "Female";
                break;
            default:
                break;
        }
        return gender;
    }

    //метод, задающий значение выбранного пола (параметра в методе)
    public void setSelectedGender(Spinner genderInput, String selectedGender) {
        selectedGender = selectedGender == null ? "" : selectedGender; //если gender == null, gender = пустой строке
        switch (selectedGender) {
            case "": //если пол не указан
                genderInput.setSelection(0);
                break;
            case "Male":
                genderInput.setSelection(1);
                break;
            case "Female":
                genderInput.setSelection(2);
                break;
            default:
                break;
        }
    }

    //метод возвращает язык в ISO формате
    //берется айди выбранного пункта в спиннере и возвращается язык по этому айди
    public String getSelectedLanguage(Spinner languageInput, String[] languagesKeys) {
        return languagesKeys[languageInput.getSelectedItemPosition()];
    }

    //метод устанавливает выбор спиннера в соответствии с языком (в виде строки), полученным с сервиса
    public void setSelectedLanguage(Spinner languageInput, String[] languagesKeys, String selectedLanguage) {
        languageInput.setSelection(indexOf(languagesKeys, selectedLanguage));
    }

    //метод возвращает текст из EditText, если таковой имеется,
    //иначе возвращается null
    public String getTextFromInput(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")){
            return null;
        }
        else {
            return editText.getText().toString();
        }
    }

    //метод нахождения индекса элемента в массиве строк
    private int indexOf(String[] a, String target)
    {
        for (int i = 0; i < a.length; i++)
            if (a[i].equals(target))
                return i;

        return -1;
    }
}
