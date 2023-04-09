package br.com.akowalski.utils;

import java.text.MessageFormat;

public class MessageFormatUtils {

    public static String format(Type type, String... args) {
        switch (type) {
            case NOT_NULL -> {
                return new MessageFormat(Type.NOT_NULL.message).format(args);
            }
            case MIN_SIZE -> {
                return new MessageFormat(Type.MIN_SIZE.message).format(args);
            }
            case MAX_SIZE -> {
                return new MessageFormat(Type.MAX_SIZE.message).format(args);
            }
            case MIN_DATE -> {
                return new MessageFormat(Type.MIN_DATE.message).format(args);
            }
            case MAX_DATE -> {
                return new MessageFormat(Type.MAX_DATE.message).format(args);
            }
            case EMAIL -> {
                return new MessageFormat(Type.EMAIL.message).format(args);
            }
            case CPF -> {
                return new MessageFormat(Type.CPF.message).format(args);
            }
        }
        return null;
    }

    public enum Type {
        NOT_NULL("The {0} field is required"),
        MIN_SIZE("The {0} field cannot be less than {1}"),
        MAX_SIZE("The {0} field cannot be greater than {1}"),
        MIN_DATE("The {0} field cannot have a date before {1} "),
        MAX_DATE("The {0} field cannot have a date after {1} "),
        EMAIL("The {0} field it is not a valid email "),
        CPF("The {0} field it is not a valid CPF ");

        private String message;

        Type(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }


}
