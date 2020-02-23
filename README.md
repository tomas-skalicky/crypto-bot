# When to use this bot

You want to invest in cryptocurrencies long term. You want to buy
them and hold them for a long time because you believe their price will grow
in time. You still want to **mitigate effects of bad market timing**, hence
you want to **buy periodically** a smaller amount of cryptocurrency for the
current price over a longer period. For this purpose you can use this bot.

# How is the bot triggered

The bot is triggered periodically by an external tool. For instance by crontab:

```shell script
FIXME
```

# How the bot works

During each execution the bot performs the following steps in the given order:

1. Checks if there are **open orders** of the configured cryptocurrency pair
on the configured exchange. If so, the bot reports the open orders as a warning
message to the user so that the user can quickly resolve the situation
manually.

1. Reports the user the last closed order and associated trades so that
the user has a overview how the last placed order was executed.

1. Checks if there is **available base currency** to buy a configured
cryptocurrency. If there is less, reports the situation to the user and stops
the execution here. If there is enough base currency, process with further
steps.

1. Retrieves the current ask price of the configured cryptocurrency pair to
be able to create a **limit buy order** with a slightly lower bid price and
hence to pay market maker fees. If the bot bought for the current ask price,
the user would need to pay market taker fees which are typically higher that
market maker fees.

# Features

## Supported exchanges

[Kraken](https://www.kraken.com/)

## Supported reporting tools

[Slack](https://slack.com/). Configured in application.properties.
