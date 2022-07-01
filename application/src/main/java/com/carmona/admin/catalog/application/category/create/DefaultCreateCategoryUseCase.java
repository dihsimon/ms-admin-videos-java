package com.carmona.admin.catalog.application.category.create;

import com.carmona.admin.catalog.domain.category.Category;
import com.carmona.admin.catalog.domain.category.CategoryGateway;
import com.carmona.admin.catalog.domain.validation.handler.Notification;
import com.carmona.admin.catalog.domain.validation.handler.ThrowsValidationHandler;
import io.vavr.API;
import io.vavr.control.Either;

import java.util.Objects;

import static io.vavr.API.Try;

public class DefaultCreateCategoryUseCase extends CreateCategoryUseCase{

    private final CategoryGateway categoryGateway;

    public DefaultCreateCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<Notification, CreateCategoryOutput> execute(final CreateCategoryCommand aCommand) {
        final var aName = aCommand.name();
        final var aDescription = aCommand.description();
        final var isActive = aCommand.isActive();
        final var aCategory = Category.newCategory(aName, aDescription, isActive);

        final var notification = Notification.create();

        aCategory.validate(notification);

        return notification.hasError() ? API.Left(notification) : create(aCategory);
    }

    private Either<Notification, CreateCategoryOutput> create(Category aCategory) {
        return Try(() -> this.categoryGateway.create(aCategory))
                .toEither()
                .bimap(Notification::create, CreateCategoryOutput::from);
    }
}
