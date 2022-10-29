package dsk.invoiceapi.domain;

import java.io.Serializable;

/**
 * Predefined document types.
 */
public enum DocType implements Serializable {
    INVOICE(1), CREDIT_NOTE(2), DEBIT_NOTE(3);

    final int code;

    DocType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * Get DocType from given numeric code.
     * @param code The numeric code.
     * @return Corresponding DocType.
     */
    public static DocType fromCode(int code) {
        return switch (code) {
            case 1 -> INVOICE;
            case 2 -> CREDIT_NOTE;
            case 3 -> DEBIT_NOTE;
            default -> null;
        };
    }
}
