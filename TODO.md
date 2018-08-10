# TODO

## compareToZero() and compareToOne() statics

Rename compareZero() to compareToZero() and add compareToOne().

Use compareToOne() in times() methods to bail out early.

```
x = 1, y = ? => return y
x = ?, y = 1 => return x
```

## BigNumBase wrappers for Java's BigInteger/BigDecimal

Create classes:

- BigIntWrap (wraps BigInteger inside of a BigNumBase)
- BigDecWrap (wraps BigDecimal inside of a BigNumBase)

Edit BigNumBaseApp to use wrapper classes instead of Object.

## BigDecBase

Finish BigDecBase modeled after BigDecimal so can use decimals in base 12.

## Produce each digit of pi

While calculating pi, be able to produce each new digit to the console.

## Add standard Java big math methods

In addition to custom ones, add standard Java BigInteger/BigDecimal methods so that you can use senpi's classes as drop-in replacements, just aliases (calls originals).
