package com;

import java.io.*;
import java.text.*;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

class IO {

    private final String[] args;
    private final StringBuilder num_sens_str = new StringBuilder();
    private final StringBuilder times_str = new StringBuilder();
    private String time_hms_le = "";
    private String time_astr_le = "";
    private int cnt_hits;
    private int cnt_hits_max_out;
    private int cnt_hits_min;
    private int cnt_str = 0;
    private File out_f;

    IO(String[] args_from_main) throws IOException, ParseException {                                               // конструктор инициализации переданных аргументов
        args = args_from_main;
        start();
    }

    private void start() throws IOException, ParseException {
        File in_f = new File(args[0]);
        Pattern pattern_in_f = Pattern.compile("^(.*\\\\.*?)?(.*?)(\\.txt)");                                      // паттерн поиска имени файла исключая расширение, даже если указана директория
        Matcher matcher_in_f = pattern_in_f.matcher(args[0]);
        matcher_in_f.find();
        out_f = new File("out__" + matcher_in_f.group(2) + "__minHits_" + args[1] + "_maxOutHits_" + args[2] + ".txt");

        cnt_hits_min = Integer.parseInt(args[1]);       // Минимальное захватываемое число хитов
        cnt_hits_max_out = Integer.parseInt(args[2]);   // Максимальное выводимое число хитов

        BufferedReader reader_obj = new BufferedReader(new InputStreamReader(new FileInputStream(in_f)));
        BufferedWriter writer_obj = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out_f)));
        runStream(reader_obj, writer_obj);

        writer_obj.close();
        Pattern pattern_out_f = Pattern.compile("^(.*?)(\\.txt)");                                                 // паттерн поиска имени файла исключая расширение
        Matcher matcher_out_f = pattern_out_f.matcher(out_f.getName());
        matcher_out_f.find();
        System.out.println();
        File out_f_new = new File(matcher_out_f.group(1) + "_str_" + cnt_str + ".txt");
        System.out.println("Count of lines was added in filename: " + out_f.renameTo(out_f_new));                                                                                 // добавляем к имени файла число полученных строк для удобства
    }

    private void runStream(BufferedReader reader_obj, BufferedWriter writer_obj) throws IOException, ParseException {
        try {
            String rd_line;
            StringBuilder buff_str;
            String curr_amp;
            String curr_le = "";
            float amps = 0;
            float avg_amps;
            int cnt_amps = 0;
            boolean first_le = true;
            boolean first_write = true;
            boolean ev_found = false;

            while ((rd_line = reader_obj.readLine()) != null) {                         // построчный проход текстового файла
                int flags_pattern = Pattern.CASE_INSENSITIVE;
                Pattern pattern_rd_line = Pattern.compile("^(LE|HT|EV)\\s+(\\d+)\\s+(\\d+:\\d+:\\d+)\\s+(\\d+,\\d+)\\s+(\\d+)\\s+(\\d+,\\d+)", flags_pattern);  // если вдруг нужен поиск до паттерна, используем .*?(паттерн)
                Matcher matcher_rd_line = pattern_rd_line.matcher(rd_line);            // инииализация матчинга

                if (matcher_rd_line.find()) {                                          // если в строке найдены совпадения по паттерну
                    String curr_num_sens = matcher_rd_line.group(5);
                    curr_amp = matcher_rd_line.group(6);
                    if (curr_amp.matches(".*,.*"))                                  // обнаружение запятой (,) для curr_amp
                        curr_amp = curr_amp.replace(',', '.');                  // замена запятой (,) на точку (.)
                    if (rd_line.matches("^[Ee][Vv].*") && !ev_found)                // если нашли event, то вывод как во время LE, пропускаем все последующие Ht и ждём только LE
                        ev_found = true;
                    if (rd_line.matches("^([Ll][Ee]).*")) {                         // обработка поступившей строки если она lE
                        time_hms_le = matcher_rd_line.group(3);                     // сохранение времен lE в отдельные переменные, т.к. их не нужно выводить в файл, они потребуются только для вычисления временного промежутка до hitа
                        time_astr_le = matcher_rd_line.group(4);
                        if (!first_le && times_str.length() > 1) {                     // впервые найденный lE выводить и обнулять не нужно, поскольку данные ещё не собраны
                            times_str.deleteCharAt(times_str.length() - 1);            // убрать пробел перед последней ; т.к. заранее знать последний hit, не перейдя к след. строке, мы не можем
                            if (cnt_hits >= cnt_hits_min) {                            // пока недосчитались до 3 хитов, ничего в файл не пишем
                                avg_amps = amps / cnt_amps;
                                buff_str = new StringBuilder(num_sens_str + "" + times_str + " " + avg_amps + " " + curr_le + ";\n");      // буфер для задачи посимвольной записи циклом
                                for (int i = 0; i < buff_str.length(); i++)
                                    writer_obj.write(buff_str.charAt(i));              // посимвольная запись в файл собранной строки buff_str, если ей не пользоваться, то на след. шагах out_f.renameTo выдаст false и файл не переименуешь
                                cnt_str++;
//                                writeStr(false, writer_obj);
                                if (first_write)                                       // запрет записи [ навсегда
                                    first_write = false;
                            }
                            num_sens_str.setLength(0);                                 // обнуление строк после обнаружения нового LE
                            times_str.setLength(0);
                            cnt_hits = 0;
                        } else {                                                      // запись первой информационной строчки для ясности
                            buff_str = new StringBuilder("LE ");
                            StringBuilder chans = new StringBuilder();
                            StringBuilder deltas = new StringBuilder();
                            for (int i = (cnt_hits_max_out - cnt_hits_min + 1); i <= cnt_hits_max_out; i++) {
                                chans.append("Ht").append(i).append(" ");
                                deltas.append("T").append(i).append(" ");
                            }
                            buff_str.append(chans).append(deltas).append("AVG_AMP LE_DSET \n");
                            for (int i = 0; i < buff_str.length(); i++)
                                writer_obj.write(buff_str.charAt(i));
                        }
                        first_le = false;
                        ev_found = false;
                        amps = 0;
                        cnt_amps = 0;
                        curr_le = matcher_rd_line.group(2);
                        if (first_write)                                                // пишем [ всегда при нахождении LE и только если никогда не было cnt_hits > cnt_hits_min. Сделано так, поскольку строка при при каждом нахождении нового LE автоматом очищается. Ведь сразу знать на каком LE число hits будет больше cnt_hits_min мы не можем
                            num_sens_str.append("[");
                        if (cnt_hits <= cnt_hits_max_out && cnt_hits_max_out > 0) {      // если задано ограничение в количестве датчиков на вывод
                            num_sens_str.append(curr_num_sens).append(" ");             // запись номера датчика во время LE события в строку num_sens
                        }
                        //System.out.println("LE" + " " + matcher_rd_line.group(4) + " " + matcher_rd_line.group(2) + " " + matcher_rd_line.group(3));
                    } else if (rd_line.matches("^[Hh][Tt].*") && !ev_found && !first_le) {    // обработка поступившей строки если она hit
                        cnt_hits++;
                        if (cnt_hits <= cnt_hits_max_out && cnt_hits_max_out > 0) {    // если задано ограничение в количестве датчиков на вывод
                            num_sens_str.append(curr_num_sens).append(" ");            // накопление номеров датчиков во время Ht событий в строку num_sens
                            times_str.append(handleTime(matcher_rd_line.group(3), matcher_rd_line.group(4))).append(" ");       // преобразование строк секунды/миллисекунды в одно число, с отслеживанием сек. перехода
                        }
                        //System.out.println("Ht" + " " + matcher_rd_line.group(4) + " " + matcher_rd_line.group(2) + " " + matcher_rd_line.group(3) + " " + time_hms_le + " " + time_astr_le + " cntr_hits " + cntr_hits);
                    }
                    if (cnt_hits <= cnt_hits_max_out && cnt_hits_max_out > 0 && !ev_found && !first_le) {
                        amps += Float.parseFloat(curr_amp);
                        cnt_amps += 1;
                    }
                }
            }
            if (first_write) {                                                        // если вдруг во входном файле ничего не найдено, то в выход ничего не пишем, действия в блоке пропускаем
                System.out.println("\nNo data found.\nFile \"" + out_f + "\" is empty.");
                writer_obj.flush();
            } else {
                if (num_sens_str.length() != 0 && times_str.length() != 0) {         // запись данных в последнюю строчку, если в конце входного файла LE так и не нашёлся
                    times_str.deleteCharAt(times_str.length() - 1);
//                    times_str.deleteCharAt(times_str.length() - 1);
                    if (cnt_hits >= cnt_hits_min) {                                  // если обнаружилось достаточное количество хитов
                        avg_amps = amps / cnt_amps;
                        buff_str = new StringBuilder(num_sens_str + "" + times_str + " " + avg_amps + " " + curr_le + "];");
                    } else
                        buff_str = new StringBuilder("];");
                    for (int i = 0; i < buff_str.length(); i++)
                        writer_obj.write(buff_str.charAt(i));                         // посимвольная запись в файл собранной строки buff_str, если ей не пользоваться, то на след. шагах out_f.renameTo выдаст false и файл не переименуется
                    cnt_str++;
                    writer_obj.flush();
                }
                System.out.println("\nThe job's is finished.\nResult data in the file \"" + out_f + "\".");
            }
        } catch (PatternSyntaxException pse) {                                          // исключение в случае несоответствия паттерну
            System.err.println("Неправильное регулярное выражение: " + pse.getMessage());
            System.err.println("Описание: " + pse.getDescription());
            System.err.println("Позиция: " + pse.getIndex());
            System.err.println("Неправильный шаблон: " + pse.getPattern());
        } catch (IllegalStateException e) {
            System.out.println("No matches for spec pattern!");
        }
    }

    private String handleTime(String time_hms_ht, String time_astr_ht) throws ParseException {
        // перевод секунд
        SimpleDateFormat hms = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);                  // шаблон для последующего перевода времени в паттерн типа SimpleDateFormat
        Date time_hms_le_date = hms.parse(time_hms_le);                                                  // перевод вх. временнОй строки в тип Date на основании паттерна "HH:mm:ss"
        Date time_hms_ht_date = hms.parse(time_hms_ht);
        long diff_milliseconds = time_hms_ht_date.getTime() - time_hms_le_date.getTime();                // перевод ЧМС в мс, сразу фиксируем разницу
////            long diff = TimeUnit.SECONDS.convert(diff_hms, TimeUnit.MILLISECONDS);                   // альтернативный вариант перевода
//        long diff_seconds = TimeUnit.MILLISECONDS.toSeconds(diff_milliseconds);                        // перевод миллисекунд в секунды

        // перевод астрономических секунд в миллисекунды

        if (time_astr_le.matches(".*,.*"))                                                         // обнаружение запятой (,) для LE
            time_astr_le = time_astr_le.replace(',', '.');                               // замена запятой (,) на точку (.)
        if (time_astr_ht.matches(".*,.*"))                                                         // обнаружение запятой (,) для HT
            time_astr_ht = time_astr_ht.replace(',', '.');                               // замена запятой (,) на точку (.)
        float time_astr_le_f = Float.parseFloat(time_astr_le);                                           // преобразование строки миллисекунд в вещественный числовой тип float
        float time_astr_ht_f = Float.parseFloat(time_astr_ht);
        float diff_time_ht = time_astr_ht_f - time_astr_le_f;
        float diff_time = diff_milliseconds + diff_time_ht;

//        try {
        if (diff_time < 0)                                                                              // разница абсолютного времени в секундах не должна быть < 0, иначе ошибка
            throw new IllegalArgumentException("\n Program emergency fault!\n Found is a negative HMS time on \"LE " + time_hms_le + " " + time_astr_le + "\" and \"Ht " + time_hms_ht + " " + time_astr_ht + "\" times.\n Incorrect data is not displayed.");
        else if (diff_milliseconds < 0)                                                                 // разница ЧМС времени не должна быть < 0, иначе ошибка
            throw new IllegalArgumentException("\n Program emergency fault!\n Found is a negative Milliseconds on \"LE " + time_hms_le + " " + time_astr_le + "\" and \"Ht " + time_hms_ht + " " + time_astr_ht + "\" times.\n Incorrect data is not displayed.");
//            }
//        catch (IllegalArgumentException e) {                                                          // позволяет программе продолжить выполнение строк после catch при перехвате throw new IllegalArgumentException()
//            System.out.println(e.getMessage());
//        }
        if (diff_time_ht < 0 || diff_milliseconds > 0)                                                  // обнаружение перехода через секунду относительно ЧМС времени (при миллисекундные значения меняются и их разница може быть < 0)
            System.out.println("Found seconds jump on \"LE " + time_hms_le + " " + time_astr_le + "\" and \"Ht " + time_hms_ht + " " + time_astr_ht + "\"");

//            if (millisecs.length() > 4)                                                               // нас интересуют только миллисекунды (10^3)
//                millisecs = millisecs.substring(0, 4);
//            return secs + "." + Float.toString(millisecs_le_f) + " ";

        DecimalFormat f = new DecimalFormat("0.0000");                                          // шаблон для последующего перевода астрономических секунд в миллисекунды с округлением
//        NumberFormat nf_out = NumberFormat.getNumberInstance(Locale.US);
//        nf_out.setMaximumFractionDigits(5);
//        nf_out.format(diff_time);
        f.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        //        if (out_times_str.matches(".*\\..*"))                                                        // обнаружение точки (.)
//            out_times_str = out_times_str.replace('.', ',');                                         // замена точки (.) на запятую (,)

        return f.format((diff_time));
    }
}