# Generated by Django 2.1.7 on 2019-03-22 06:16

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('users', '0001_initial'),
        ('invitations', '0001_initial'),
    ]

    operations = [
        migrations.AddField(
            model_name='invitation',
            name='user_id',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='user_id', to='users.UserProfile'),
        ),
    ]
