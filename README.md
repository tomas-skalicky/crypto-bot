| Branch Name | Build Status | Notes |
| ----------- | ------------ | ----- |
| master | [![Build Status](https://travis-ci.com/tomas-skalicky/crypto-bot.svg?branch=master)](https://travis-ci.com/tomas-skalicky/crypto-bot) | |
| fb-tom-introduction-of-jpms | [![Build Status](https://travis-ci.com/tomas-skalicky/crypto-bot.svg?branch=fb-tom-introduction-of-jpms)](https://travis-ci.com/tomas-skalicky/crypto-bot) | Experimental branch to introduce JPMS to the project |

# When to use this bot

You want to invest in cryptocurrencies long term. You want to buy them and hold
them for a long time because you believe their price will grow in time. You
still want to **mitigate effects of bad market timing**, hence you want to **buy
periodically** a smaller amount of cryptocurrency for the current price over a
longer period. For this purpose you can use this bot.

# How is the bot triggered

The bot is triggered periodically by an external tool. For instance by crontab:

```shell script
2 20 * * * cd <project_home> && ./gradlew clean build run --args='--baseCurrency EUR --quoteCurrency BTC --volumeInBaseCurrencyToInvestPerRun 50 --tradingPlatformName kraken --tradingPlatformApiKey "<your_trading_platform_api_key>" --tradingPlatformApiSecret "<your_trading_platform_api_secret>" --slackWebhookUrl "<your_slack_webhook_url_to_be_notified>"' -Dorg.gradle.java.home=<java_11_or_later_home>
```

## Input parameters

* baseCurrency ... Currency to sell, to buy quoted currency
* quoteCurrency ... Currency to buy
* volumeInBaseCurrencyToInvestPerRun ... How much of the base currency will be
  intended to be invested into the market currency per run of this bot
* tradingPlatformName ... Name of trading platform. Currently supported
  platforms: kraken
* tradingPlatformApiKey ... Key for private part of trading platform API
* tradingPlatformApiSecret ... Secret for private part of trading platform API
* offsetRatioOfLimitPriceToBidPriceInDecimal ... Offset ratio of limit price to
  the bid price. In Decimal. Sample value: 0.01 (= limit price 1% below the bid
  price)
* slackWebhookUrl ... Slack Webhook to notify the user about placing of orders,
  open and closed orders, etc.

# How the bot works

During each execution the bot performs the following steps in the given order:

1. Checks if there is **enough currency available** which should be sold. If
   there is enough currency which should be sold, continues with the step 2. If
   there is less currency than required (see input parameters), reports the
   situation to the user and continues with the step 3.

1. Retrieves the current bid price of the configured cryptocurrency pair to be
   able to create a **limit buy order** with a slightly lower bid price and
   hence to pay market maker fees. If the bot bought for the current ask price,
   the user would need to pay market taker fees which are typically higher that
   market maker fees.

1. Reports **orders open** on the given trading platform to the user. This helps
   among others to inform the user about details of orders placed in the
   preceding steps as well as older orders (assuming that the orders were not
   closed right away).

1. Reports **orders closed** in the last 72 hours on the given trading platform
   to the user so that the user has an overview how the last placed orders were
   executed.

# Features

## Supported trading platforms

[Kraken](https://www.kraken.com/). ID of the trading platform is passed to the
bot as an argument. See input parameters.

## Supported reporting tools

[Slack](https://slack.com/). Slack Webhook URL is passed to the bot as an
argument. See input parameters.

# Implementation

The program is divided into 4 tiers:

* application ...
    * contains the point where the program starts (the `main` method),
    * parses input arguments,
    * orchestrates the whole program which means all other tiers are
      instantiated and initialized,
    * contains the rough skeleton of what the bot should do and
    * delegates the execution of each skeleton steps to the business logic tier.
* business logic ...
    * contains the core of the program, i.e. what the bot should buy, how much
      of it, for which price, under which conditions,
    * places orders on the given trading platform via "connector facades",
    * decides which data needs to be retrieved from the given trading platform
      and asks the "connector facades" for them,
    * decides when the user should be notified via Slack and sends them a
      message via "connector facades" and
    * does not work with domain models specific to trading platforms (classes
      with suffix `Dto`).
* connector facades ...
    * takes requests represented in business logic domain model (classes with
      suffix `Bo`) from "business logic", converts the requests to domain models
      specific to particular trading platforms (classes with suffix `Dto`) and
      passes the converted requests to "connectors". Similarly for responses.
      This helps to decouple the common business logic and trading platform
      specifics.
    * has no low-level knowledge about issuing of requests to (resp. receiving
      of responses from) trading platforms.
* connectors ...
    * knows APIs of trading platforms and uses them,
    * does not work with business logic domain model (classes with suffix `Bo`).

## Technologies

The program is written **without any dependency injection** solution to keep the
source code understandable for the broader audience.

The code is buildable with and runnable on the currently latest JDK available on
Travis CI. See
https://docs.travis-ci.com/user/languages/java/#using-java-10-and-later

## Coding conventions

### `var` used only when contributing to code readability

The reserved type name `var` is allowed to be used only in situations when its
usage contributes to the code readability, or at least not decrease code
readability. It is unwanted to create a situation that the developer would need
to track down what type a variable is of.

Therefore, use `var` **only**

1. when there is an instance creation on the right side of the expression,
   nothing else (e.g. `var message = "success";`, `var count = 3;`,
   `var user = new User("Tomas", "Skalicky", MaritalStatusEnum.MARRIED);`).
1. when there is a `String` concatenation on the right side of the expression,
   nothing else (e.g.
   `var orderPlacedMessage = priceOrderType.getLabel() + " order to " + orderType.getLabel();`)
   .
1. when there is a factory method invoked, containing no nested factory method
   invocation, followed by no other method invoked on the factory method result,
   on the right side of the expression (
   e.g. `var dateTime = LocalDateTime.of(2020, 4, 26, 11, 30);`,
   `var amount = BigDecimal.valueOf(4988);`,
   `var facade = mock(TradingPlatformPublicApiFacade.class);`,
   `var marketName = Collections.singletonMap("pair", "XBTEUR");`)
1. when there is an enum value on the right side of the expression, nothing
   else (e.g. `var orderType = OrderTypeBoEnum.BUY;`)
1. when there is a `static final` variable (aka constant) on the right side of
   the expression, nothing else (e.g. `var volume = BigDecimal.ZERO;`)
1. when the entire right side of the expression is casted to a certain type (
   e.g.
   `var askArray = (List<String>) inputEntry.getValue().get("a");`)

Do **NOT** use `var` in any other context, like in the following ones:

* `StringBuilder builder = new StringBuilder("user is ").append(user
  );` (violates the rule 3)
* `var orderFlags = ImmutableList.<String>builder().build();` (violates the rule
  3)
* `Collections.unmodifiableMap(Map.of("trades", String.valueOf(trades),
  "start", String.valueOf(from)));` (violates the rule 3)

## Never-ending TODOs:

1. Upgrade to the latest JDK available on Travis CI.
   See https://docs.travis-ci.com/user/languages/java/#using-java-10-and-later
1. Upgrade to the latest versions of used libraries
