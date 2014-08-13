% Valang Validator under the hood

#  Valang Validator under the hood

## How to Convert Valang syntax Expression into ValidationRule Object model?

org.springmodules.validation.valang.parser.ValangParser is the key class that will help on this task.

If you are able to construct a valid valang-syntax expression from some other sources, you can use ValangParser to parse these kinds of expressions into Valang's 
Object model. for example:

~~~~~~~ {.java}
Errors errors = ...;
Object target = ...;
				
ValangParser parser = new ValangParser("{ <key> : <expression> : <error_message> [ : <error_key> [ : <error_args> ] ]}");
try {
    Collection<ValidationRule> rules = parser.parseValidation();
    if(CollectionUtils.isNotEmpty(rules))
    {
        Iterator<ValidationRule> iter = rules.iterator();
        while(iter.hasNext()){
            ValidationRule rule = iter.next();
            rule.validate(target, errors);
        }
    }
} catch (ParseException e) {
	// handle exception here.
}
~~~~~~~

 				
with sample code above, I think you can figure out how the ValangValidator class do its work.

Since you can “setValang(String valang) ”, you can “setCustomFunctions(..) ”, in the “validate(Object target, Errors errors) ” method, the ValangValidator only need use ValangParser to parse the expression set via “setValang(String) ” method. After a collection of ValidationRule is available, the left things is almost the same like code above.

Of course, since ValangValidator use ValangParser to do the parsing things, you can use ValangValidator or its super class, that's, org.springmodules.validation.valang.parser.SimpleValangBased , to do the same thing. I mean, to parse the valang expression.

## Custom ValangValidator or ValidationRule

when I want to add GlobalError support for ROMA framework, I found that as if Valang doesn't support such GlobalError expression things, so I have to find another way.

In a valang-syntax expression, the first token is the <key>, it's mandatory. But for a global error, it's meaningless. so even we provide a dummy <key> value, and make the expression-parsing pass, when we invoke the “#validate(target, errors) ” method of ValidationRule, an exception will still be raised, because, the ValidationRule can't find a property on the target object. In order to fix this, we have to break down the “#validate(target, errors) ” method's logic. Here is what I will do.

If we inspect the type of the ValidationRule returned from “parser.parseValidation() ”, we will find that it's type is org.springmodules.validation.valang.predicates.BasicValidationRule . This is the default value object that hold every part of the parsed Valang expression. Since we can get everything with it, we then can filter the returned collection of ValidationRule. The code seems like:


~~~~~~~ {.java}
ValangValidator validator = new ValangValidator();
        validator.setValang("");
        @SuppressWarnings("unchecked")
        Collection<ValidationRule> rules = validator.getRules();
        @SuppressWarnings("unchecked")
        Collection<ValidationRule> globalErrorRules = CollectionUtils.transformedCollection(rules, new Transformer() {
            public Object transform(Object arg) {
                final BasicValidationRule rule = (BasicValidationRule)arg;
                return new ValidationRule() {
                    public void validate(Object target, org.springframework.validation.Errors errors) {
                        String errorKey = rule.getErrorKey();
                        String message = rule.getErrorMessage();
                        @SuppressWarnings("unchecked")
                        Collection args = rule.getErrorArgs();
                        if(CollectionUtils.isEmpty(args))
                        {
                            errors.reject(errorKey, message);
                        }
                        else
                        {
                            @SuppressWarnings("unchecked")
                            Object[] argArray = args.toArray(new Object[args.size()]);
                            errors.reject(errorKey, argArray, message);
                        }
                    }
                };
            }
        });
~~~~~~~

 
since FiledError is added with “#rejectValue(..) ”, we use “#reject(..) ” to fill GlobalError to Errors . After these rules are applied to the target object, the corresponding global errors will be available. You can pull them in you view via spring's RequestContext or other way you resort to.