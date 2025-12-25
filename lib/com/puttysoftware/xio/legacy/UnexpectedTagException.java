package com.puttysoftware.xio.legacy;

import java.io.IOException;

public class UnexpectedTagException extends IOException {
    private static final long serialVersionUID = 23250505322336L;

    public UnexpectedTagException(final String msg) {
	super(msg);
    }
}
