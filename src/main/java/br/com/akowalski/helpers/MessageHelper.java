package br.com.akowalski.helpers;

import java.text.MessageFormat;

public class MessageHelper {

    public static String format(Type type, String... args) {
        return switch (type) {
            case NOT_NULL -> new MessageFormat(Type.NOT_NULL.message).format(args);
            case MIN_SIZE -> new MessageFormat(Type.MIN_SIZE.message).format(args);
            case MAX_SIZE -> new MessageFormat(Type.MAX_SIZE.message).format(args);
            case MIN_DATE -> new MessageFormat(Type.MIN_DATE.message).format(args);
            case MAX_DATE -> new MessageFormat(Type.MAX_DATE.message).format(args);
            case EMAIL -> new MessageFormat(Type.EMAIL.message).format(args);
            case CPF -> new MessageFormat(Type.CPF.message).format(args);
        };
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
