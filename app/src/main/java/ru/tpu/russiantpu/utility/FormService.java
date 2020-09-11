package ru.tpu.russiantpu.utility;

import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import ru.tpu.russiantpu.R;

//класс для получения строк из формы
public class FormService {

    public String getSelectedGender(RadioGroup genderInput) {
        String gender = null;
        switch (genderInput.getCheckedRadioButtonId()) {
            case R.id.gender_male:
                gender = "Male";
                break;
            case R.id.gender_female:
                gender = "Female";
                break;
            default:
                break;
        }
        return gender;
    }

    public void setSelectedGender(String gender, RadioGroup genderInput) {
        gender = gender == null ? "" : gender; //если gender == null, gender = пустой строке
        switch (gender) {
            case "Male":
                ((RadioButton)genderInput.findViewById(R.id.gender_male)).setChecked(true);
                break;
            case "Female":
                ((RadioButton)genderInput.findViewById(R.id.gender_female)).setChecked(true);
                break;
            default:
                ((RadioButton)genderInput.findViewById(R.id.gender_none)).setChecked(true);
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
