# Anonimatron – fnx fork

Fork of [realrolfje/anonimatron](https://github.com/realrolfje/anonimatron) with deterministic
Czech anonymizers in `io.fnx.anonimatron.anonymizer`. Maven coordinates
`io.fnx:anonimatron:1.17-fnx-4`, Java 17.

**Deterministic:** the replacement is computed from HMAC-SHA256(salt, original value), so the same
database anonymized twice with the same secret salt gives the same result, without keeping a
synonym file. Set the salt in the configuration (`<configuration salt="...">`) or, better, pass
it on the command line (`-salt`) from a CI secret. The fnx anonymizers refuse to run without it.

Register the anonymizers you use with `<anonymizerclass>` and use their types:

| Type | Class | Result |
|---|---|---|
| `FIRSTNAME` | `FirstNameAnonymizer` | Czech first name of the same gender |
| `LASTNAME` | `LastNameAnonymizer` | Czech surname in the same gender form; Novák and Nováková stay one family |
| `FULLNAME` | `FullNameAnonymizer` | names of one or more people in text, titles kept; matches `FIRSTNAME` and `LASTNAME` |
| `COMPANYNAME` | `CompanyNameAnonymizer` | unique made-up company with the original legal form; a person's name (sole trader) like `FULLNAME` |
| `ICO` | `IcoNumberAnonymizer` | valid unique IČO; a birth number stays a birth number |
| `DIC` | `DicNumberAnonymizer` | `CZ` + anonymized IČO (matches the `ICO` column) or `CZ` + birth number |
| `RC` | `PersonalIdentificationNumberAnonymizer` | valid unique rodné číslo, same gender, born ±2 years, same format |
| `PHONE` | `PhoneAnonymizer` | same format, country code and first digit |
| `EMAIL` | `EmailAnonymizer` | unique `anonym.…@example.com` for every address in the value |
| `BANKACCOUNT` | `BankAccountAnonymizer` | Czech account passing the mod 11 check, same bank code |
| `NUMBERMASK` | `NumberMaskAnonymizer` | digits replaced, letters and separators kept, unique |
| `STREET` | `StreetNameAnonymizer` | Czech street, house number shape kept |
| `TOWN`, `ADDRESS`, `BANKNAME`, `PROJECTNAME`, `LOREM` | | made-up values from word lists |

Unique values (IČO, birth numbers, e-mails, companies, `NUMBERMASK`) resolve a collision with the
next attempt; with the same data the result is the same on every run.

# Anonimatron [![Maven version](https://img.shields.io/maven-central/v/com.rolfje.anonimatron/anonimatron.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.rolfje.anonimatron%22%20AND%20a:%22anonimatron%22) [![CI](https://github.com/realrolfje/anonimatron/actions/workflows/ci.yml/badge.svg?branch=develop)](https://github.com/realrolfje/anonimatron/actions/workflows/ci.yml)

Did you ever have that problem where you needed "production data" to find a bug
or do performance tests outside of the client’s production environment? Are
you worried about protecting that data? Do you add screenshots to your bug
reports? Can you live with surrogate data with the same properties? Then this
tool is for you. 

Running Anonimatron is as simple as downloading, unzipping and running 
`anonimatron.sh` or `anonimatron.bat`.

Please find all documentation, downloads and quickstarts at the 
[Anonimatron official homepage](https://realrolfje.github.io/anonimatron/).  

For coding, tinkering and other fun articles go to [www.rolfje.com](https://www.rolfje.com/).
