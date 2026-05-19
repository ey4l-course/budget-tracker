INSERT INTO transactions (
    username,
    name,
    amount,
    default_category,
    user_defined_category,
    default_regular_interval,
    user_defined_regular,
    comment,
    system_flag,
    category_type
)
    VALUES (
        :username,
        :name,
        :amount,
        :defaultCategory,
        :userDefinedCategory,
        :defaultRegularInterval,
        :userDefinedRegular,
        :comment,
        :systemFlag,
        :categoryType
    )
        ON DUPLICATE KEY UPDATE system_flag = 1;