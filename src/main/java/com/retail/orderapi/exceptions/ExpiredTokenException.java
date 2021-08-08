package com.retail.orderapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Expired Auth Token. Please login!")
public class ExpiredTokenException extends RuntimeException {
}
