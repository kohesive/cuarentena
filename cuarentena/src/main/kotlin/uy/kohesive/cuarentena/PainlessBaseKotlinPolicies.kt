package uy.kohesive.cuarentena

import uy.kohesive.cuarentena.policy.AccessTypes
import uy.kohesive.cuarentena.policy.PolicyAllowance
import uy.kohesive.cuarentena.policy.toPolicy

internal object KotlinBootstrapPolicies {

    val primitiveArrayAccessPolicy = listOf('B', 'C', 'D', 'F', 'I', 'J', 'S', 'V', 'Z').map {
        PolicyAllowance.ClassLevel.ClassAccess("[$it", setOf(AccessTypes.ref_Class_Instance))
    }.toPolicy()

    val painlessKotlinBootstrapPolicy = primitiveArrayAccessPolicy + (listOf(
            // This one can't be generated by painless as it needs an actual Class instance, and 'java.lang.Synthetic' is pure synthetic (no pun intended)
            PolicyAllowance.ClassLevel.ClassAccess("java.lang.Synthetic", setOf(AccessTypes.ref_Class)),

            PolicyAllowance.ClassLevel.ClassMethodAccess("kotlin.jvm.internal.Intrinsics", "checkExpressionValueIsNotNull", "(Ljava/lang/Object;Ljava/lang/String;)V", setOf(AccessTypes.call_Class_Static_Method)),
            PolicyAllowance.ClassLevel.ClassMethodAccess("kotlin.jvm.internal.Intrinsics", "checkParameterIsNotNull", "(Ljava/lang/Object;Ljava/lang/String;)V", setOf(AccessTypes.call_Class_Static_Method)),
            PolicyAllowance.ClassLevel.ClassMethodAccess("kotlin.jvm.internal.Intrinsics", "throwNpe", "()V", setOf(AccessTypes.call_Class_Static_Method)),

            // kotlin.Unit
            // This is a tough one as we don't want to replace it with Void
            PolicyAllowance.ClassLevel.ClassFieldAccess("kotlin.Unit", "INSTANCE", "Lkotlin/Unit;", setOf(AccessTypes.read_Class_Static_Field)),

            // kotlin misc
            PolicyAllowance.ClassLevel.ClassAccess("org.jetbrains.annotations.NotNull", setOf(AccessTypes.ref_Class)),
            PolicyAllowance.ClassLevel.ClassAccess("org.jetbrains.annotations.Nullable", setOf(AccessTypes.ref_Class))
    )).toPolicy()

}